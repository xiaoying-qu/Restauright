package hu.ait.restauright.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.restauright.BuildConfig
import hu.ait.restauright.Data.restaurant_result.RestaurantResult
import hu.ait.restauright.network.RestaurantAPI
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RestaurantUiState {
    object Init : RestaurantUiState
    object Loading : RestaurantUiState
    data class Success(val Restaurant: RestaurantResult) : RestaurantUiState
    data class Error(val errorMsg: String) : RestaurantUiState
}

@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    val restaurantAPI: RestaurantAPI
) : ViewModel() {
    var restaurantUiState: RestaurantUiState by mutableStateOf(RestaurantUiState.Init)
    fun getRestaurants() {
        restaurantUiState = RestaurantUiState.Loading
        viewModelScope.launch {
            restaurantUiState = try {
                val result = restaurantAPI.getRestaurants(BuildConfig.RESTAURANT_LOCATION, BuildConfig.RESTAURANT_TERM, BuildConfig.RESTAURANT_API_KEY)
                Log.d("DEBUG", "getWeather result: $result")
                RestaurantUiState.Success(result)
            }
            catch (e: Exception) {
                Log.d("ERROR", "getWeather: $e")
                RestaurantUiState.Error(e.message!!)
            }
        }

    }
}