package com.airline.checkin.domain.usecase.boarding

import com.airline.checkin.domain.repository.BoardingPassRepository
import javax.inject.Inject

class DownloadBoardingPassPdfUseCase @Inject constructor(
    private val boardingPassRepository: BoardingPassRepository
) {
    suspend operator fun invoke(checkinId: String): Result<ByteArray> =
        boardingPassRepository.downloadBoardingPassPdf(checkinId)
}
