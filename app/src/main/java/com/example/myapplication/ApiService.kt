package com.example.myapplication

import Event
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/Intersections")
    fun getIntersections(): Call<List<Intersection>>

    @GET("api/TrafficLights/intersection/{intersectionId}")
    fun getTrafficLightsByIntersectionId(@Path("intersectionId") intersectionId: Int): Call<List<TrafficLight>>

    @GET("api/Events/intersection/{intersectionId}")
    fun getEventsByIntersectionId(@Path("intersectionId") intersectionId: Int): Call<List<Event>>

    @POST("api/Events")
    suspend fun postEvent(
        @Query("intersectionId") intersectionId: Int,
        @Body newEvent: Event
    ): Event
}