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

    // Ensures session codes are unique: If the code is already in the database, will randomly generate a new one and try again
    private fun createNewSession(code: String, id: String, zipCode: String, callback: (Session?) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val session = childSnapshot.getValue(Session::class.java)
                    val sessionCode = session?.code

                    if (sessionCode != null && sessionCode == code) {
                        val newCode = (100000..999999).random().toString()
                         createNewSession(newCode, id, zipCode, callback)
                    }
                }

                val session = Session(id, code, zipCode)
                callback(session)

            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
    fun createSession(zipCode: String, callback: (Session?) -> Unit) {
        homeUiState = HomeUiState.Loading

        try {
            val id = database.push().key!!
            val code = (100000..999999).random().toString()
            createNewSession(code, id, zipCode) { result ->
                if (result == null) {
                    homeUiState = HomeUiState.Error("Something went wrong")

                }
                else {
                    database.child(id).setValue(result)
                        .addOnCompleteListener {
                            Log.d("DEBUG", "createSession: complete $result ")
                            homeUiState = HomeUiState.RegisterSuccess
                            callback(result)
                        }
                        .addOnFailureListener {
                            homeUiState = HomeUiState.Error(it.message)
                        }
                }
            }

        } catch (e: Exception) {
            Log.d("createSession", "createSession: Error $e")
            homeUiState = HomeUiState.Error(e.message)
        }
    }

    suspend fun joinSession(code: String, callback: (Session) -> Unit) {
        homeUiState = HomeUiState.Loading

        try {
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        val session = childSnapshot.getValue(Session::class.java)
                        val sessionCode = session?.code

                        if (sessionCode != null && sessionCode == code) {
                            // Call the callback with the result
                            homeUiState = HomeUiState.LoginSuccess
                            callback(session)
                            return
                        }
                    }

                    // Call the callback with the result
                    homeUiState = HomeUiState.Error("No session with that code. Enter a new code and try again.")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle potential errors or interruptions in the database query
                    Log.e("Firebase", "Error querying database: ${databaseError.message}")

                    // Call the callback with an error message
                    homeUiState = HomeUiState.Error("Something went wrong")
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