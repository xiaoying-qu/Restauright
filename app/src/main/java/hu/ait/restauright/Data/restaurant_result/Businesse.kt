package hu.ait.restauright.Data.restaurant_result


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Businesse(
    @SerialName("alias")
    val alias: String? = null,
    @SerialName("categories")
    val categories: List<Category>? = null,
    @SerialName("coordinates")
    val coordinates: Coordinates? = null,
    @SerialName("display_phone")
    val displayPhone: String? = null,
    @SerialName("distance")
    val distance: Double? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("is_closed")
    val isClosed: Boolean? = null,
    @SerialName("location")
    val location: Location? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("phone")
    val phone: String? = null,
    @SerialName("price")
    val price: String? = null,
    @SerialName("rating")
    val rating: Double? = null,
    @SerialName("review_count")
    val reviewCount: Int? = null,
    @SerialName("transactions")
    val transactions: List<String>? = null,
    @SerialName("url")
    val url: String? = null
)