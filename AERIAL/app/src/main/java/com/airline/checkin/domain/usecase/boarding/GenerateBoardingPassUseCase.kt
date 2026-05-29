package com.airline.checkin.domain.usecase.boarding

import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.repository.CheckInRepository
import javax.inject.Inject

class GenerateBoardingPassUseCase @Inject constructor(
    private val checkInRepository: CheckInRepository
) {
    suspend operator fun invoke(checkinId: String): Result<BoardingPass> =
        checkInRepository.confirmCheckIn(checkinId)
}
