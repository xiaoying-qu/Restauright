package hu.ait.restauright.screen.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import hu.ait.restauright.Data.Restaurant
import hu.ait.restauright.Data.Session
import hu.ait.restauright.Data.restaurant_result.Businesse
import kotlinx.coroutines.tasks.await


class ResultsViewModel : ViewModel() {

    var resultsUiState: ResultsUiState by mutableStateOf(ResultsUiState.Init)

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    init {
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("sessions")
    }

    fun getResults(sessionId: String, callback: (List<Restaurant?>) -> Unit ) {

        val sessionReference = database.child(sessionId)
        sessionReference.child("restaurants")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val sortedRestaurants = dataSnapshot.children
                        .map { it.getValue(Restaurant::class.java) }
                        .sortedByDescending { it?.votes }

                    callback(sortedRestaurants)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle potential errors or interruptions in the database query
                    Log.e("Firebase", "Error querying database: ${databaseError.message}")
                }
            })
    }
}

sealed interface ResultsUiState {
    object Init : ResultsUiState
    object Loading : ResultsUiState
    object LoginSuccess : ResultsUiState
    object RegisterSuccess : ResultsUiState
    data class Error(val error: String?) : ResultsUiState
}
