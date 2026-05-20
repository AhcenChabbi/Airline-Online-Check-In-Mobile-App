package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.SpecialRequestItemDto
import com.airline.checkin.data.remote.dto.SpecialRequestsRequestDto
import com.airline.checkin.domain.model.SpecialRequest

object SpecialRequestMapper {
    fun toRequest(request: SpecialRequest): SpecialRequestItemDto =
            SpecialRequestItemDto(
                    category = request.category,
                    detail = request.detail,
                    notes = request.notes
            )

    fun toRequest(requests: List<SpecialRequest>): SpecialRequestsRequestDto =
            SpecialRequestsRequestDto(requests = requests.map { toRequest(it) })
}
