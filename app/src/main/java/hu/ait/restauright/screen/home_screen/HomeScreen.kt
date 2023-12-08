package hu.ait.restauright.screen.home_screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.restauright.screen.login.HomeScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (
    modifier: Modifier = Modifier,
    onNavigateToRestaurants: () -> Unit,
    homeScreenViewModel: HomeScreenViewModel = viewModel()
) {
    var userText by rememberSaveable {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userText,
            onValueChange = { userText = it }
        )
        Button(onClick = {
            coroutineScope.launch {
                homeScreenViewModel.joinSession(userText) { result ->
                    if (result != "Code does not exist") {
                        onNavigateToRestaurants()
                    }
                    else {
                        Log.d("DEBUG", "HomeScreen: No session")
                    }
                }
            }
        }) {
            Text(text = "Join a group with a code")
        }
        Spacer(modifier = modifier.height(50.dp))
        Text(
            text = "OR",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = modifier.height(50.dp))
        Button(onClick = {
            homeScreenViewModel.createSession()
            onNavigateToRestaurants()
        }) {
            Text(text = "Create a new group")
        }
    }
}