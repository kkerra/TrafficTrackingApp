package com.example.myapplication

import Event
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.Color
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomSheetState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                var showAddEventScreen by remember { mutableStateOf(false) }

                if (showAddEventScreen) {
                    AddEventScreen(onEventAdded = {
                        showAddEventScreen = false
                    }, onClose = {
                        showAddEventScreen = false
                    })
                } else {
                    IntersectionListScreen(onAddEventClick = {
                        showAddEventScreen = true
                    })
                }
            }
        }
    }
}

val cardBackgroundColor = Color(0xFFE0E0E0)
val cardContentColor = Color.Black

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntersectionListScreen(onAddEventClick: () -> Unit) {
    val viewModel: IntersectionViewModel = viewModel()
    val intersections = viewModel.intersections
    val errorMessage = viewModel.errorMessage
    val isLoading = viewModel.isLoading

    val selectedIntersection = viewModel.selectedIntersection
    val trafficLights = viewModel.trafficLights
    val events = viewModel.events

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Intersections") },
                actions = {
                    IconButton(onClick = onAddEventClick) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Event")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage.isNotEmpty()) {
                Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            } else {
                if (intersections.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(intersections) { intersection ->
                            IntersectionItem(
                                intersection = intersection,
                                onIntersectionClick = {
                                    viewModel.onIntersectionClicked(intersection)
                                }
                            )
                            if (selectedIntersection == intersection) {
                                IntersectionDetails(
                                    trafficLights = trafficLights,
                                    events = events,
                                    onClose = { viewModel.clearSelectedIntersection() }
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No intersections found.")
                    }
                }
            }
        }
    }
}

@Composable
fun IntersectionItem(intersection: Intersection, onIntersectionClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onIntersectionClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor,
            contentColor = cardContentColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "ID: ${intersection.intersectionId}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Latitude: ${intersection.latitude}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Longitude: ${intersection.longitude}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Lanes: ${intersection.lanesAmount}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Turning Lanes: ${if (intersection.isTurningLanes) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Update Date: ${intersection.updateDate ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun IntersectionDetails(trafficLights: List<TrafficLight>, events: List<Event>, onClose: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor,
            contentColor = cardContentColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Traffic Lights", style = MaterialTheme.typography.titleMedium)
            TrafficLightList(trafficLights = trafficLights)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Events", style = MaterialTheme.typography.titleMedium)
            EventList(events = events)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onClose) {
                Text("Close")
            }
        }
    }
}

@Composable
fun TrafficLightList(trafficLights: List<TrafficLight>?) {
    Column(modifier = Modifier.padding(8.dp)) {
        if (trafficLights == null || trafficLights.isEmpty()) {
            Text("No traffic lights found for this intersection.")
        } else {
            trafficLights.forEach { trafficLight ->
                TrafficLightItem(trafficLight = trafficLight)
            }
        }
    }
}

@Composable
fun TrafficLightItem(trafficLight: TrafficLight) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "ID: ${trafficLight.trafficLightId}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Type: ${trafficLight.type}", style = MaterialTheme.typography.bodySmall)
            Text(text = "State: ${trafficLight.state}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun EventList(events: List<Event>?) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    Column(modifier = Modifier.padding(8.dp)) {
        if (events == null || events.isEmpty()) { // Check for null
            Text("No events found for this intersection.")
        } else {
            events.forEach { event ->
                EventItem(event = event, dateFormat = dateFormat)
            }
        }
    }
}

@Composable
fun EventItem(event: Event, dateFormat: SimpleDateFormat) {
    val trafficImpactRed = Color(0xFFF44336)
    val trafficImpactYellow = Color(0xFFFFEB3B)
    val trafficImpactGreen = Color(0xFF4CAF50)
    val trafficImpactColor = when (event.trafficImpactLevel) {
        5 -> trafficImpactRed
        in 3..4 -> trafficImpactYellow
        in 1..2 -> trafficImpactGreen
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor,
            contentColor = cardContentColor
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(trafficImpactColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "Type: ${event.type}", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "Start Time: ${formatDateString(event.startTime, dateFormat) ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "End Time: ${formatDateString(event.endTime, dateFormat) ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Description: ${event.description ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Traffic Impact: ${event.trafficImpactLevel}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(onEventAdded: () -> Unit, onClose: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val viewModel: IntersectionViewModel = viewModel()

    var type by remember { mutableStateOf(TextFieldValue("")) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) }
    var startTime by remember { mutableStateOf(TextFieldValue("")) }
    var endTime by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var trafficImpactLevel by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Event") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Type") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                label = { Text("Start Time (yyyy-MM-dd HH:mm)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = endTime,
                onValueChange = { endTime = it },
                label = { Text("End Time (yyyy-MM-dd HH:mm)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = trafficImpactLevel,
                onValueChange = { trafficImpactLevel = it },
                label = { Text("Traffic Impact Level (1-5)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                coroutineScope.launch {
                    try {
                        val newEvent = Event(
                            eventId = 0,
                            type = type.text,
                            startTime = startTime.text,
                            endTime = endTime.text,
                            description = description.text,
                            trafficImpactLevel = trafficImpactLevel.text.toIntOrNull() ?: 1
                        )

                        viewModel.postEvent(newEvent)

                        onEventAdded()

                        Toast.makeText(context, "Event added successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error adding event: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text("Add Event")
            }
        }
    }
}

fun formatDateString(dateString: String?, dateFormat: SimpleDateFormat): String? {
    return try {
        if (dateString != null) {
            val date = dateFormat.parse(dateString)
            dateFormat.format(date)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}