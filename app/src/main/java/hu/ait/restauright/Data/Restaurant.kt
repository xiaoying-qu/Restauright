package hu.ait.restauright.Data

import hu.ait.restauright.Data.restaurant_result.Businesse

data class Restaurant(
    val id: String = "",
    val restaurant: Businesse = Businesse(null),
    val votes: Int = 0
)
