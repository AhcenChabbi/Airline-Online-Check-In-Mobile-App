package com.airline.checkin.domain.usecase.boarding

import com.airline.checkin.core.services.CheckInSessionStore
import com.airline.checkin.core.utils.LocalPdfGenerator
import com.airline.checkin.domain.repository.BoardingPassRepository
import javax.inject.Inject

class DownloadBoardingPassPdfUseCase @Inject constructor(
    private val boardingPassRepository: BoardingPassRepository,
    private val sessionStore: CheckInSessionStore
) {
    suspend operator fun invoke(checkinId: String): Result<ByteArray> = runCatching {
        val boardingPass = boardingPassRepository.getBoardingPass(checkinId).getOrThrow()
        val session = sessionStore.session.value

        val passengerName = if (session.passenger != null && session.checkIn?.id == checkinId) {
            session.passenger.name
        } else {
            val payloadPassenger = boardingPass.offlinePayload?.passenger
            listOf(payloadPassenger?.firstName, payloadPassenger?.lastName)
                .filter { !it.isNullOrBlank() }
                .joinToString(" ")
                .ifBlank { "Passenger" }
        }

        val flightNumber = boardingPass.offlinePayload?.flight?.flightNumber
            ?: session.flight?.flightNumber
            ?: "AER123"

        val from = boardingPass.offlinePayload?.flight?.origin
            ?: session.flight?.originIata
            ?: "CDG"

        val fromCity = if (session.flight != null && session.flight.originIata == from) {
            session.flight.originCity
        } else {
            if (from == "CDG") "Paris" else from
        }

        val to = boardingPass.offlinePayload?.flight?.destination
            ?: session.flight?.destinationIata
            ?: "JFK"

        val toCity = if (session.flight != null && session.flight.destinationIata == to) {
            session.flight.destinationCity
        } else {
            if (to == "JFK") "New York" else to
        }

        val date = if (session.flight != null && session.checkIn?.id == checkinId) {
            session.flight.date
        } else {
            boardingPass.offlinePayload?.flight?.departureAt ?: ""
        }

        val seat = boardingPass.offlinePayload?.seat?.seatCode
            ?: session.selectedSeat?.seatCode
            ?: "12A"

        val boardingTime = if (session.flight != null && session.checkIn?.id == checkinId) {
            session.flight.departureTime
        } else {
            boardingPass.offlinePayload?.flight?.departureAt ?: ""
        }

        val bookingRef = if (session.booking != null && session.checkIn?.id == checkinId) {
            session.booking.bookingReference
        } else {
            session.checkIn?.bookingId ?: "BOOKING"
        }

        val qrCodeData = boardingPass.qrCodeData

        LocalPdfGenerator.generateBoardingPassPdf(
            passengerName = passengerName,
            flightNumber = flightNumber,
            from = from,
            fromCity = fromCity,
            to = to,
            toCity = toCity,
            date = date,
            seat = seat,
            boardingTime = boardingTime,
            bookingRef = bookingRef,
            qrCodeData = qrCodeData
        )
    }
}
