package hu.ait.restauright.network

import hu.ait.restauright.Data.restaurant_result.RestaurantResult
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RestaurantAPI {
    @GET("/v3/businesses/search")
    suspend fun getRestaurantsbyZip(@Query("location") location: String,
                                    @Query("term") term: String,
                                    @Header("Authorization") apiKey: String,
                               ) : RestaurantResult

    @GET("/v3/businesses/search")
    suspend fun getRestaurantsbyCoord(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("term") term: String,
        @Header("Authorization") apiKey: String,
    ) : RestaurantResult
}
