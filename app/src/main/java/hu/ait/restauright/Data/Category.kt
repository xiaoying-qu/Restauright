package hu.ait.restauright.Data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    @SerialName("alias")
    val alias: String? = null,
    @SerialName("title")
    val title: String? = null
)