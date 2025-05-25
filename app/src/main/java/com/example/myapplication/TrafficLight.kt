package com.example.myapplication

import com.google.gson.annotations.JsonAdapter
import java.time.LocalDate

data class TrafficLight(
    val trafficLightId: Int,
    val intersectionId: Int,
    val type: String,
    val state: String,
    @JsonAdapter(LocalDateAdapter::class)
    val installationDate: LocalDate?
)