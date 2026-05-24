package com.airline.checkin.domain.model

data class SpecialRequest(
        val id: String = "",
        val checkinId: String = "",
        val category: String = "OTHER",
        val detail: String = "",
        val notes: String? = null,
        val createdAt: String? = null
) {
    val description: String
        get() = detail

    constructor(id: String, description: String) : this(id = id, detail = description)
}
