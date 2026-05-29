package com.airline.checkin.domain.repository

import com.airline.checkin.domain.model.BoardingPass

interface BoardingPassRepository {
    suspend fun getBoardingPass(checkinId: String): Result<BoardingPass>
    suspend fun downloadBoardingPassPdf(checkinId: String): Result<ByteArray>
}
