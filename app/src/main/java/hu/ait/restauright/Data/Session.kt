package hu.ait.restauright.Data

data class Session(
    val id: String = "",
    val code: String = "",
    val zipCode: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

data class CoordSession(
    val id: String = "",
    val code: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0
)
