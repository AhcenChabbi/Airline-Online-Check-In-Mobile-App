package com.airline.checkin.data.repository

import com.airline.checkin.data.local.mapper.EntityMapper.toEntity
import com.airline.checkin.data.local.room.dao.BookingDao
import com.airline.checkin.data.local.room.dao.FlightDao
import com.airline.checkin.data.local.room.dao.PassengerDao
import com.airline.checkin.data.remote.api.FlightApi
import com.airline.checkin.data.remote.dto.BookingLookupRequestDto
import com.airline.checkin.data.remote.mapper.BookingMapper
import com.airline.checkin.domain.model.BookingLookup
import com.airline.checkin.domain.repository.FlightRepository
import javax.inject.Inject

class FlightRepositoryImpl @Inject constructor(
    private val flightApi: FlightApi,
    private val flightDao: FlightDao,
    private val bookingDao: BookingDao,
    private val passengerDao: PassengerDao
) : FlightRepository {
    override suspend fun lookupBooking(
        bookingReference: String,
        lastName: String
    ): Result<BookingLookup> = runCatching {
        val response = flightApi.lookupBooking(
            BookingLookupRequestDto(
                bookingReference = bookingReference,
                lastName = lastName
            )
        )
        val bookingLookup = BookingMapper.toDomain(response)

        // Cache flight, booking and passenger data locally
        val flightEntity = bookingLookup.flight.toEntity()
        flightDao.save(flightEntity)

        val bookingEntity = com.airline.checkin.data.local.room.entity.BookingEntity(
            id = bookingLookup.bookingId,
            userId = null,
            flightId = flightEntity.id,
            bookingReference = bookingLookup.bookingReference,
            lastName = lastName,
            status = try {
                com.airline.checkin.data.local.room.entity.BookingStatus.valueOf(bookingLookup.status)
            } catch (e: Exception) {
                com.airline.checkin.data.local.room.entity.BookingStatus.PENDING
            }
        )
        bookingDao.save(bookingEntity)

        passengerDao.saveAll(bookingLookup.passengers.map { it.toEntity() })

        bookingLookup
    }
}
