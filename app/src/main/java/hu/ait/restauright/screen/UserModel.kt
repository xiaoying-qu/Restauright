package hu.ait.restauright.screen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UserModel @Inject constructor(
) : ViewModel() {

    private val _numVotesRemaining = MutableStateFlow(5)
    val numVotesRemaining = _numVotesRemaining.asStateFlow()

    fun getNumVotes(): Flow<Int> {
        return numVotesRemaining
    }

    fun useAVote() {
        if (_numVotesRemaining.value > 0) {
            _numVotesRemaining.value -= 1
        }
    }
}