package hu.ait.restauright.Data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Region(
    @SerialName("center")
    val center: Center? = null
)