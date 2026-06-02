package com.airline.checkin.domain.usecase.checkin

import com.airline.checkin.domain.model.CheckIn
import com.airline.checkin.domain.model.PassportScanData
import com.airline.checkin.domain.repository.CheckInRepository
import javax.inject.Inject

class ScanPassportUseCase @Inject constructor(
    private val checkInRepository: CheckInRepository
) {
    suspend operator fun invoke(
        checkinId: String,
        data: PassportScanData
    ): Result<CheckIn> = checkInRepository.submitPassport(checkinId, data)
}
