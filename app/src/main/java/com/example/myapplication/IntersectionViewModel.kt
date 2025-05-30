package com.example.myapplication

import Event
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:5291/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private val apiService = retrofit.create(ApiService::class.java)

@RequiresApi(Build.VERSION_CODES.O)
class IntersectionViewModel : ViewModel() {

    var intersections: List<Intersection> by mutableStateOf(emptyList())
        private set

    var errorMessage: String by mutableStateOf("")
        private set

    var isLoading: Boolean by mutableStateOf(false)
        private set

    var selectedIntersection: Intersection? by mutableStateOf(null)
        private set

    var trafficLights: List<TrafficLight> by mutableStateOf(emptyList())
        private set

    var events: List<Event> by mutableStateOf(emptyList())
        private set

    var showTrafficLights: Boolean by mutableStateOf(false)
        private set

    var showEvents: Boolean by mutableStateOf(false)

    init {
        loadIntersections()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun loadIntersections() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            val apiService = RetrofitClient.instance
            val call = apiService.getIntersections()

            call.enqueue(object : Callback<List<Intersection>> {
                override fun onResponse(call: Call<List<Intersection>>, response: Response<List<Intersection>>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        intersections = response.body() ?: emptyList()
                    } else {
                        errorMessage = "HTTP Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<Intersection>>, t: Throwable) {
                    isLoading = false
                    errorMessage = "Network error: ${t.message}"
                }
            })
        }
    }

    fun loadTrafficLightsForIntersection(intersectionId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            val apiService = RetrofitClient.instance
            val call = apiService.getTrafficLightsByIntersectionId(intersectionId)

            call.enqueue(object : Callback<List<TrafficLight>> {
                override fun onResponse(call: Call<List<TrafficLight>>, response: Response<List<TrafficLight>>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        trafficLights = response.body() ?: emptyList()
                    } else {
                        errorMessage = "HTTP Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<TrafficLight>>, t: Throwable) {
                    isLoading = false
                    errorMessage = "Network error: ${t.message}"
                }
            })
        }
    }

    fun loadEventsForIntersection(intersectionId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            val apiService = RetrofitClient.instance
            val call = apiService.getEventsByIntersectionId(intersectionId)

            call.enqueue(object : Callback<List<Event>> {
                override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        events = response.body() ?: emptyList()
                    } else {
                        errorMessage = "HTTP Error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    isLoading = false
                    errorMessage = "Network error: ${t.message}"
                }
            })
        }
    }

    fun onIntersectionClicked(intersection: Intersection) {
        selectedIntersection = intersection
        loadTrafficLightsForIntersection(intersection.intersectionId)
        loadEventsForIntersection(intersection.intersectionId)
    }

    fun showTrafficLights() {
        showTrafficLights = true
        showEvents = false
    }

    fun showEvents() {
        showEvents = true
        showTrafficLights = false
    }

    fun clearSelectedIntersection() {
        selectedIntersection = null
        trafficLights = emptyList()
        events = emptyList()
        showTrafficLights = false
        showEvents = false
    }

    fun postEvent(newEvent: Event, intersectionId: Int) {
        viewModelScope.launch {
            try {
                val createdEvent = apiService.postEvent(intersectionId, newEvent)
                loadEventsForIntersection(selectedIntersection?.intersectionId ?: intersectionId) // Используйте ID текущего перекрестка

            } catch (e: Exception) {
                errorMessage = "Failed to post event: ${e.message}"
            }
        }
    }
}