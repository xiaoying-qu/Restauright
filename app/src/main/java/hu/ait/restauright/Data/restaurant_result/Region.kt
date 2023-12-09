package hu.ait.restauright.Data.restaurant_result


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Region(
    @SerialName("center")
    val center: Center? = null
)