package hu.ait.restauright.Data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Center(
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null
)