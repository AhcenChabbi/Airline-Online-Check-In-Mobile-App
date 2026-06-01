package com.airline.checkin.domain.usecase.checkin

import com.airline.checkin.domain.model.CheckIn
import com.airline.checkin.domain.repository.CheckInRepository
import javax.inject.Inject

class StartCheckInUseCase @Inject constructor(
    private val checkInRepository: CheckInRepository
) {
    suspend operator fun invoke(bookingId: String, passengerId: String): Result<CheckIn> =
        checkInRepository.initiateCheckIn(bookingId, passengerId)
}
