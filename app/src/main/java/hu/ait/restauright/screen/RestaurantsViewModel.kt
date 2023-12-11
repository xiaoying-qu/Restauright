package hu.ait.restauright.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.ait.restauright.BuildConfig
import hu.ait.restauright.Data.Restaurant
import hu.ait.restauright.Data.Session
import hu.ait.restauright.Data.restaurant_result.Businesse
import hu.ait.restauright.Data.restaurant_result.RestaurantResult
import hu.ait.restauright.network.RestaurantAPI
import hu.ait.restauright.screen.login.HomeUiState
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

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    init {
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("sessions")
    }
    fun getRestaurants(location: String) {
        restaurantUiState = RestaurantUiState.Loading
        viewModelScope.launch {
            restaurantUiState = try {
                val result = restaurantAPI.getRestaurants(location, BuildConfig.RESTAURANT_TERM, BuildConfig.RESTAURANT_API_KEY)
                RestaurantUiState.Success(result)
            }
            catch (e: Exception) {
                Log.d("ERROR", "getWeather: $e")
                RestaurantUiState.Error(e.message!!)
            }
        }
    }

    fun addRestaurauntsToSession(restaurants: List<Businesse>?, sessionId: String) {
        val sessionReference = database.child(sessionId).child("restaurants")
        for (restaurant in restaurants!!) {
            // Check if the restaurant with the specified key already exists
            sessionReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // Restaurant with the key does not exist, add it
                        val restaurantKey = restaurant.id
                        val restaurantObject = restaurantKey?.let {
                            Restaurant(
                                id = it,
                                restaurant = restaurant,
                                votes = 0
                            )
                        }
                        sessionReference.child(restaurantKey.orEmpty()).setValue(restaurantObject)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("ERROR", "addRestaurauntsToSession: Error")
                }
            })
        }
    }


    fun voteForRestaurant(restaurant: Businesse, sessionId: String) {
        val restaurant_id = restaurant.id
        if (restaurant_id != null) {
            val sessionReference = database.child(sessionId).child("restaurants").child(restaurant_id)
            sessionReference.child("votes").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val currentVotes = dataSnapshot.getValue(Long::class.java) ?: 0

                    // Update the votes count
                    sessionReference.child("votes").setValue(currentVotes + 1)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Log.e("Firebase", "Error updating votes", databaseError.toException())
                }
            })

        }
    }
}