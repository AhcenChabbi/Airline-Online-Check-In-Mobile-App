package com.airline.checkin.domain.usecase.boarding

import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.repository.BoardingPassRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetOfflineBoardingPassUseCase @Inject constructor(
        private val repository: BoardingPassRepository
) {
    operator fun invoke(checkinId: String): Flow<BoardingPass?> =
            repository.observeBoardingPass(checkinId)
}
