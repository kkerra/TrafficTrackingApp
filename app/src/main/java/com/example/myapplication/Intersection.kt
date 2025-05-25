package com.example.myapplication

data class Intersection(
    val intersectionId: Int,
    val latitude: Double,
    val longitude: Double,
    val lanesAmount: Int,
    val isTurningLanes: Boolean,
    val updateDate: String?,
)
