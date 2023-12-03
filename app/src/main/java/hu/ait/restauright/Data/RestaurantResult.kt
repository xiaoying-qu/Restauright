package hu.ait.restauright.Data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantResult(
    @SerialName("businesses")
    val businesses: List<Businesse>? = null,
    @SerialName("region")
    val region: Region? = null,
    @SerialName("total")
    val total: Int? = null
)