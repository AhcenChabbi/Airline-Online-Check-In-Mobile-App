package com.airline.checkin.domain.repository

import com.airline.checkin.domain.model.BoardingPass
import java.io.File
import kotlinx.coroutines.flow.Flow

interface BoardingPassRepository {
    suspend fun getBoardingPass(checkinId: String): Result<BoardingPass>

    fun observeBoardingPass(checkinId: String): Flow<BoardingPass?>

    suspend fun downloadBoardingPassPdf(checkinId: String): Result<ByteArray>
}