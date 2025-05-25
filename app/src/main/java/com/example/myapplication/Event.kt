import java.util.Date

data class Event(
    val eventId: Int,
    val type: String,
    val startTime: String?,
    val endTime: String?,
    val description: String?,
    val trafficImpactLevel: Int
)