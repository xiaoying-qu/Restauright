package hu.ait.restauright.screen.results

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.restauright.screen.login.ResultsUiState
import hu.ait.restauright.screen.login.ResultsViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.AnnotatedString
import hu.ait.restauright.Data.Restaurant
import hu.ait.restauright.Data.restaurant_result.Businesse
import hu.ait.restauright.screen.UserModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen (
    modifier: Modifier = Modifier,
    sessionId: String,
    resultsViewModel: ResultsViewModel = viewModel()
) {

    var restaurants: List<Restaurant?> by rememberSaveable {
        mutableStateOf(emptyList())
    }

    resultsViewModel.getResults(sessionId) { result ->
        restaurants = result
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(modifier = Modifier) {
            items(restaurants) {
                restaurantCard(restaurant = it)
            }
        }
    }
}

@Composable
fun restaurantCard(
    restaurant: Restaurant?
) {
    Column {
        Card( modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
        ) {
            Text(text = "${restaurant!!.restaurant.name}")
            Text(text = "${restaurant.votes}")
        }
    }

}