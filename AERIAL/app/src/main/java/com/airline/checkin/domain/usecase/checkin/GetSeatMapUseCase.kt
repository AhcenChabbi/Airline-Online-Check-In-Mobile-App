package com.airline.checkin.domain.usecase.checkin

import com.airline.checkin.domain.model.Seat
import com.airline.checkin.domain.repository.CheckInRepository
import javax.inject.Inject

class GetSeatMapUseCase @Inject constructor(
    private val checkInRepository: CheckInRepository
) {
    suspend operator fun invoke(checkinId: String): Result<List<Seat>> =
        checkInRepository.getSeatMap(checkinId)
}
