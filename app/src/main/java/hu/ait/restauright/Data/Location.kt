package hu.ait.restauright.Data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    @SerialName("address1")
    val address1: String? = null,
    @SerialName("address2")
    val address2: String? = null,
    @SerialName("address3")
    val address3: String? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("country")
    val country: String? = null,
    @SerialName("display_address")
    val displayAddress: List<String>? = null,
    @SerialName("state")
    val state: String? = null,
    @SerialName("zip_code")
    val zipCode: String? = null
)