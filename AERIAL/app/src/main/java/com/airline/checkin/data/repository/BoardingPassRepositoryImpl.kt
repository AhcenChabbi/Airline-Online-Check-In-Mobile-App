package com.airline.checkin.data.repository

import android.content.Context
import com.airline.checkin.core.utils.LocalPdfGenerator
import com.airline.checkin.data.local.mapper.EntityMapper.toDomain
import com.airline.checkin.data.local.mapper.EntityMapper.toEntity
import com.airline.checkin.data.local.room.dao.BoardingPassDao
import com.airline.checkin.data.local.room.dao.BookingDao
import com.airline.checkin.data.local.room.dao.FlightDao
import com.airline.checkin.data.local.room.dao.PassengerDao
import com.airline.checkin.data.local.room.dao.SeatDao
import com.airline.checkin.data.remote.api.BoardingPassApi
import com.airline.checkin.data.remote.mapper.BoardingPassMapper
import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.repository.BoardingPassRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BoardingPassRepositoryImpl
@Inject
constructor(
        private val api: BoardingPassApi,
        private val boardingPassDao: BoardingPassDao,
        private val flightDao: FlightDao,
        private val bookingDao: BookingDao,
        private val passengerDao: PassengerDao,
        private val seatDao: SeatDao,
        @ApplicationContext private val context: Context
) : BoardingPassRepository {

    override suspend fun getBoardingPass(checkinId: String): Result<BoardingPass> =
            runCatching {
                try {
                    val response = api.getBoardingPass(checkinId)
                    val boardingPass = BoardingPassMapper.toDomain(response.data)
                    saveBoardingPassDependencies(boardingPass)
                    boardingPassDao.save(boardingPass.toEntity())
                    boardingPass
                } catch (e: Exception) {
                    // Try loading from local cache before propagating the error
                    val cached = boardingPassDao.getByCheckinOnce(checkinId)
                    cached?.toDomain() ?: throw e
                }
            }

    private suspend fun saveBoardingPassDependencies(boardingPass: BoardingPass) {
        val payload = boardingPass.offlinePayload ?: return

        // 1. Save Flight Entity
        val flightId = payload.flight.flightNumber
        val flightEntity = com.airline.checkin.data.local.room.entity.FlightEntity(
                id = flightId,
                flightNumber = payload.flight.flightNumber,
                airlineCode = payload.flight.flightNumber.take(2),
                airlineName = "AERIAL",
                originIata = payload.flight.origin,
                originCity = payload.flight.origin,
                destIata = payload.flight.destination,
                destinationCity = payload.flight.destination,
                departureAt = runCatching { Instant.parse(payload.flight.departureAt).toEpochMilli() }.getOrElse { System.currentTimeMillis() },
                arrivalAt = runCatching { Instant.parse(payload.flight.arrivalAt).toEpochMilli() }.getOrElse { System.currentTimeMillis() },
                aircraftType = "A320",
                status = com.airline.checkin.data.local.room.entity.FlightStatus.SCHEDULED
        )
        flightDao.save(flightEntity)

        // 2. Save Booking Entity
        val bookingId = boardingPass.checkinId
        val bookingEntity = com.airline.checkin.data.local.room.entity.BookingEntity(
                id = bookingId,
                userId = null,
                flightId = flightId,
                bookingReference = boardingPass.checkinId.take(6).uppercase(),
                lastName = payload.passenger.lastName ?: "",
                status = com.airline.checkin.data.local.room.entity.BookingStatus.CHECKED_IN
        )
        bookingDao.save(bookingEntity)

        // 3. Save Passenger Entity
        val passengerEntity = com.airline.checkin.data.local.room.entity.PassengerEntity(
                id = boardingPass.passengerId,
                bookingId = bookingId,
                firstName = payload.passenger.firstName ?: "",
                lastName = payload.passenger.lastName ?: "",
                passengerType = com.airline.checkin.data.local.room.entity.PassengerType.ADULT,
                isPrimary = true
        )
        passengerDao.save(passengerEntity)

        // 4. Save Seat Entity
        val seatCode = payload.seat.seatCode
        val rowNumber = seatCode.filter { it.isDigit() }.toIntOrNull() ?: 1
        val columnLetter = seatCode.filter { it.isLetter() }
        val seatEntity = com.airline.checkin.data.local.room.entity.SeatEntity(
                id = boardingPass.seatId,
                flightId = flightId,
                seatCode = seatCode,
                rowNumber = rowNumber,
                columnLetter = columnLetter,
                seatClass = try {
                    com.airline.checkin.data.local.room.entity.SeatClass.valueOf(payload.seat.seatClass.uppercase())
                } catch (e: Exception) {
                    com.airline.checkin.data.local.room.entity.SeatClass.ECONOMY
                },
                type = com.airline.checkin.data.local.room.entity.SeatType.STANDARD
        )
        seatDao.save(seatEntity)
    }

    override fun observeBoardingPass(checkinId: String): Flow<BoardingPass?> =
            boardingPassDao.getByCheckin(checkinId).map { entity -> entity?.toDomain() }

    override suspend fun downloadBoardingPassPdf(checkinId: String): Result<ByteArray> =
            runCatching {
                try {
                    val responseBody = api.downloadBoardingPassPdf(checkinId)
                    val file = File(context.cacheDir, "boarding-pass-$checkinId.pdf")
                    responseBody.byteStream().use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    val cached = boardingPassDao.getByCheckinOnce(checkinId)
                    if (cached != null) {
                        boardingPassDao.updatePdfUrl(cached.id, file.toURI().toString())
                    }

                    file.readBytes()
                } catch (e: Exception) {
                    // Remote PDF failed — generate locally from the cached boarding pass data
                    val cached = boardingPassDao.getByCheckinOnce(checkinId)
                            ?: throw Exception("No boarding pass data available to generate PDF.")

                    val boardingPass = cached.toDomain()
                    val payload = boardingPass.offlinePayload

                    val passengerName = listOf(
                            payload?.passenger?.firstName,
                            payload?.passenger?.lastName
                    ).filter { !it.isNullOrBlank() }.joinToString(" ").ifBlank { "Passenger" }

                    val pdfBytes = LocalPdfGenerator.generateBoardingPassPdf(
                            passengerName = passengerName,
                            flightNumber = payload?.flight?.flightNumber ?: "",
                            from = payload?.flight?.origin ?: "",
                            fromCity = payload?.flight?.origin ?: "",
                            to = payload?.flight?.destination ?: "",
                            toCity = payload?.flight?.destination ?: "",
                            date = payload?.flight?.departureAt ?: "",
                            seat = payload?.seat?.seatCode ?: "",
                            boardingTime = payload?.flight?.departureAt ?: "",
                            bookingRef = checkinId.take(6).uppercase(),
                            qrCodeData = boardingPass.qrCodeData
                    )

                    val file = File(context.cacheDir, "boarding-pass-$checkinId.pdf")
                    file.writeBytes(pdfBytes)
                    boardingPassDao.updatePdfUrl(cached.id, file.toURI().toString())

                    pdfBytes
                }
            }
}
