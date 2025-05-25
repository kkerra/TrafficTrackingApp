package com.example.myapplication

import Event
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/Intersections")
    fun getIntersections(): Call<List<Intersection>>

    @GET("api/TrafficLights/intersection/{intersectionId}")
    fun getTrafficLightsByIntersectionId(@Path("intersectionId") intersectionId: Int): Call<List<TrafficLight>>

    @GET("api/Events/intersection/{intersectionId}") // Replace with your API endpoint
    fun getEventsByIntersectionId(@Path("intersectionId") intersectionId: Int): Call<List<Event>>

    @POST("api/Events")
    suspend fun postEvent(@Body newEvent: Event): Event
}