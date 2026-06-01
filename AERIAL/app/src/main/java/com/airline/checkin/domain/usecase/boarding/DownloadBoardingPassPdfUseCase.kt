package com.airline.checkin.domain.usecase.boarding

import com.airline.checkin.domain.repository.BoardingPassRepository
import java.io.File
import javax.inject.Inject

class DownloadBoardingPassPdfUseCase @Inject constructor(
        private val repository: BoardingPassRepository
) {
    suspend operator fun invoke(checkinId: String): Result<File> =
            repository.downloadBoardingPassPdf(checkinId)
}
