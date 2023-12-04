package hu.ait.restauright.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (
    modifier: Modifier = Modifier,
    onNavigateToRestaurants: () -> Unit
) {
    var userText by rememberSaveable {
        mutableStateOf("")
    }

    Column {
        TextField(
            value = userText,
            onValueChange = { userText = it}
        )
        Button(onClick = {
            onNavigateToRestaurants()
        }) {
            Text(text = "Submit")
        }
    }
}