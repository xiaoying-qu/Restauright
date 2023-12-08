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
import hu.ait.restauright.Data.Session
import kotlinx.coroutines.tasks.await


class HomeScreenViewModel : ViewModel() {

    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Init)

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    init {
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("sessions")
    }

    fun createSession() {
        homeUiState = HomeUiState.Loading

        try {
            val id = database.push().key!!
            val code = database.push().key!!
            val session = Session(
                id, code
            )
            database.child(id).setValue(session)
                .addOnCompleteListener {
                    homeUiState = HomeUiState.RegisterSuccess
                }
                .addOnFailureListener {
                    homeUiState = HomeUiState.Error(it.message)
                }
        } catch (e: Exception) {
            homeUiState = HomeUiState.Error(e.message)
        }
    }

    suspend fun joinSession(code: String, callback: (String) -> Unit) {
        homeUiState = HomeUiState.Loading

        try {
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        val session = childSnapshot.getValue(Session::class.java)
                        val sessionCode = session?.code
                        Log.d("Session", "Session: $session, Code: $sessionCode")

                        if (sessionCode != null && sessionCode == code) {
                            // Call the callback with the result
                            callback(session.id)
                            return
                        }
                    }

                    // Code doesn't exist, handle accordingly
                    // TODO: Implement logic for when the code doesn't exist

                    // Call the callback with the result
                    callback("Code does not exist")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle potential errors or interruptions in the database query
                    Log.e("Firebase", "Error querying database: ${databaseError.message}")

                    // Call the callback with an error message
                    callback("Error querying database")
                }
            })
        } catch (e: java.lang.Exception) {
            homeUiState = HomeUiState.Error(e.message)
            null
        }
    }

}

sealed interface HomeUiState {
    object Init : HomeUiState
    object Loading : HomeUiState
    object LoginSuccess : HomeUiState
    object RegisterSuccess : HomeUiState
    data class Error(val error: String?) : HomeUiState
}