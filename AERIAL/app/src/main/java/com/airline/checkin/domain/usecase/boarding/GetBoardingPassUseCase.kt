package com.airline.checkin.domain.usecase.boarding

import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.repository.BoardingPassRepository
import javax.inject.Inject

class GetBoardingPassUseCase @Inject constructor(
    private val boardingPassRepository: BoardingPassRepository
) {
    suspend operator fun invoke(checkinId: String): Result<BoardingPass> =
        boardingPassRepository.getBoardingPass(checkinId)
}
