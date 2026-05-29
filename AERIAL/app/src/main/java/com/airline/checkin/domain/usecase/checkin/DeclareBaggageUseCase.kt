package com.airline.checkin.domain.usecase.checkin

import com.airline.checkin.domain.model.Baggage
import com.airline.checkin.domain.repository.CheckInRepository
import javax.inject.Inject

class DeclareBaggageUseCase @Inject constructor(
    private val checkInRepository: CheckInRepository
) {
    suspend operator fun invoke(checkinId: String, bags: List<Baggage>): Result<Int> =
        checkInRepository.declareBaggage(checkinId, bags)
}
