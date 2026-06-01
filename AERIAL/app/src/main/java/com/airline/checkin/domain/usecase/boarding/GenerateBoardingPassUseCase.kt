package com.airline.checkin.domain.usecase.boarding

import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.repository.BoardingPassRepository
import javax.inject.Inject

class GenerateBoardingPassUseCase @Inject constructor(
        private val repository: BoardingPassRepository
) {
    suspend operator fun invoke(checkinId: String): Result<BoardingPass> =
            repository.getBoardingPass(checkinId)
}
