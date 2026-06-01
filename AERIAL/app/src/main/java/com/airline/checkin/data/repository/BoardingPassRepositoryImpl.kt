package com.airline.checkin.data.repository

import com.airline.checkin.data.remote.api.BoardingPassApi
import com.airline.checkin.data.remote.mapper.BoardingPassMapper
import com.airline.checkin.domain.model.BoardingPass
import com.airline.checkin.domain.repository.BoardingPassRepository
import javax.inject.Inject

class BoardingPassRepositoryImpl @Inject constructor(
    private val boardingPassApi: BoardingPassApi
) : BoardingPassRepository {
    override suspend fun getBoardingPass(checkinId: String): Result<BoardingPass> = runCatching {
        val response = boardingPassApi.getBoardingPass(checkinId)
        BoardingPassMapper.toDomain(response.data)
    }

    override suspend fun downloadBoardingPassPdf(checkinId: String): Result<ByteArray> =
        runCatching {
            boardingPassApi.downloadBoardingPassPdf(checkinId).bytes()
        }
}
