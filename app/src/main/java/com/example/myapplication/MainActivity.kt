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

val cardBackgroundColor = Color.White
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
                title = { Text("Перекрестки") },
                actions = {
                    IconButton(onClick = onAddEventClick) {
                        Icon(Icons.Filled.Add, contentDescription = "Добавить событие")
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
                Text(text = "Ошибка: $errorMessage", color = MaterialTheme.colorScheme.error)
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
                        Text("Перекрестков не найдено.")
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
            Text(text = "Номер перекрестка: ${intersection.intersectionId}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Широта: ${intersection.latitude}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Долгота: ${intersection.longitude}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Количество полос: ${intersection.lanesAmount}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Наличие поворотных полос: ${if (intersection.isTurningLanes) "Да" else "Нет"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Дата последнего обновления: ${intersection.updateDate ?: "Не определено"}", style = MaterialTheme.typography.bodyMedium)
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
            Text(text = "Светофоры:", style = MaterialTheme.typography.titleMedium)
            TrafficLightList(trafficLights = trafficLights)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Событие", style = MaterialTheme.typography.titleMedium)
            EventList(events = events)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onClose) {
                Text("Закрыть")
            }
        }
    }
}

@Composable
fun TrafficLightList(trafficLights: List<TrafficLight>?) {
    Column(modifier = Modifier.padding(8.dp)
        .background(Color.White)) {
        if (trafficLights == null || trafficLights.isEmpty()) {
            Text("Не найдено светофоров для перекрестков")
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Номер светофора: ${trafficLight.trafficLightId}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Тип: ${trafficLight.type}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Статус: ${trafficLight.state}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun EventList(events: List<Event>?) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    Column(modifier = Modifier.padding(8.dp)) {
        if (events == null || events.isEmpty()) {
            Text("Событий для перекрестка не найдено.")
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
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) }

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
                Text(text = "Тип: ${event.type}", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "Начало: ${formatDateString(event.startTime, dateFormat) ?: "Не определено"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Конец: ${formatDateString(event.endTime, dateFormat) ?: "Не определено"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Описание: ${event.description ?: "Не определено"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Влияние на трафик (1-5): ${event.trafficImpactLevel}",
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
    var intersectionId by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить событие") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Тип") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                label = { Text("Начало (yyyy-MM-dd HH:mm)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = endTime,
                onValueChange = { endTime = it },
                label = { Text("Конец (yyyy-MM-dd HH:mm)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = trafficImpactLevel,
                onValueChange = { trafficImpactLevel = it },
                label = { Text("Влияние на трафик (1-5)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = intersectionId,
                onValueChange = { intersectionId = it },
                label = { Text("Номер перекрестка") },
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

                        viewModel.postEvent(
                            newEvent = newEvent,
                            intersectionId = intersectionId.text.toIntOrNull() ?: 0
                        )

                        onEventAdded()
                        Toast.makeText(context, "Событие успешно добавлено", Toast.LENGTH_SHORT).show()

                    } catch (e: Exception) {
                        Toast.makeText(context, "Ошибка при добавлении: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text("Добавление")
            }
        }
    }
}

@Composable
fun <T> DataList(items: List<T>, itemContent: @Composable (item: T) -> Unit) {
    Column {
        items.forEach { item ->
            itemContent(item)
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