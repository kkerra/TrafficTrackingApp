package com.example.myapplication

data class Intersection(
    val intersectionId: Int,
    val latitude: Double, // Use Double for decimal
    val longitude: Double,
    val lanesAmount: Int,
    val isTurningLanes: Boolean,
    val updateDate: String?, // String? or Date?  Depends on your API's JSON format.  Use Date if it's an ISO-8601 date string.
    //val trafficLights: List<TrafficLight>, // You may or may not need these nested lists right now
    //val events: List<Event>
)
