package com.airline.checkin.data.remote.mapper

import com.airline.checkin.data.remote.dto.BaggageItemDto
import com.airline.checkin.data.remote.dto.BaggageRequestDto
import com.airline.checkin.domain.model.Baggage

object BaggageMapper {
    fun toRequest(baggage: Baggage): BaggageItemDto =
            BaggageItemDto(
                    bagType = baggage.bagType,
                    quantity = baggage.quantity,
                    weightKg = baggage.weightKg
            )

    fun toRequest(bags: List<Baggage>): BaggageRequestDto =
            BaggageRequestDto(bags = bags.map { toRequest(it) })
}
