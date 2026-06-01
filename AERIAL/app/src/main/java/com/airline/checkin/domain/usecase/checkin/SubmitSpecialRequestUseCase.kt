package com.airline.checkin.domain.usecase.checkin

import com.airline.checkin.domain.model.SpecialRequest
import com.airline.checkin.domain.repository.CheckInRepository
import javax.inject.Inject

class SubmitSpecialRequestUseCase @Inject constructor(
    private val checkInRepository: CheckInRepository
) {
    suspend operator fun invoke(
        checkinId: String,
        requests: List<SpecialRequest>
    ): Result<String> = checkInRepository.submitSpecialRequests(checkinId, requests)
}
