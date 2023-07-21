package com.example.spaceshare.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

enum class ReservationStatus {
    PENDING,
    APPROVED,
    DECLINED,
    CANCELLED,
    COMPLETED
}

enum class DeclareItemType {
    ClOTHING,
    BOOKS_AND_DOCUMENTS,
    FURNITURE,
    SPORT_AND_RECREATIONAL,
    APPLIANCE,
    MEMENTOS,
    DAILY_NECESSARY
}

inline fun <reified T : Enum<T>> Int.toEnum(): T? {
    return enumValues<T>().firstOrNull { it.ordinal == this }
}

inline fun <reified T : Enum<T>> T.toInt(): Int {
    return this.ordinal
}

data class Reservation(
    val id: String? = null,
    val hostId: String? = null,
    val clientId: String? = null,
    val listingId: String? = null,
    val totalCost: Double = 0.0,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val spaceRequested: Double = 0.0,
    val status: ReservationStatus = ReservationStatus.PENDING,
    val location: String = "",
    val listingTitle: String = "",
    val previewPhoto: String? = null,
    val items: MutableList<DeclareItemType>? = null
//    val rating:Int? = null
) {
}