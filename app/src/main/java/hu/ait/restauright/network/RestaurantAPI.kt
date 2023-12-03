package hu.ait.restauright.network

import hu.ait.restauright.Data.RestaurantResult
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RestaurantAPI {
    @GET("/v3/businesses/search")
    suspend fun getRestaurants(@Query("location") location: String,
                               @Query("term") term: String,
                               @Header("Authorization") apiKey: String,
                               ) : RestaurantResult
}