package com.airline.checkin.domain.usecase.checkin

import com.airline.checkin.domain.model.Flight
import com.airline.checkin.domain.model.Passenger
import com.airline.checkin.domain.repository.CheckInRepository
import javax.inject.Inject

class ConfirmDetailsUseCase @Inject constructor(
    private val checkInRepository: CheckInRepository
) {
    suspend operator fun invoke(checkinId: String): Result<Pair<Passenger, Flight>> =
        checkInRepository.confirmDetails(checkinId)
}
