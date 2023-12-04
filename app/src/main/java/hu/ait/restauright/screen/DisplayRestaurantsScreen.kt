package hu.ait.restauright.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableTarget
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.restauright.Data.Businesse
import hu.ait.restauright.Data.RestaurantResult

@Composable
fun DisplayRestaurantsScreen (
    modifier: Modifier = Modifier,
    restaurantsViewModel: RestaurantsViewModel = hiltViewModel(),
    onNavigateToResults: () -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        restaurantsViewModel.getRestaurants()
    }

    Column {
        when (restaurantsViewModel.restaurantUiState) {
            is RestaurantUiState.Init -> {}
            is RestaurantUiState.Loading -> CircularProgressIndicator()
            is RestaurantUiState.Success -> ResultScreen((restaurantsViewModel.restaurantUiState as RestaurantUiState.Success).Restaurant, onNavigateToResults = onNavigateToResults)
            is RestaurantUiState.Error -> Text(text = "Error: ${(restaurantsViewModel.restaurantUiState as RestaurantUiState.Error).errorMsg}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    restaurant: RestaurantResult,
    userModel: UserModel = hiltViewModel(),
    onNavigateToResults: () -> Unit
) {
    val restaurants by rememberSaveable {
        mutableStateOf(restaurant.businesses)
    }
    val numVotes by userModel.getNumVotes().collectAsState(5)

    Column {
        TopAppBar(
            title = { Text(text = "Number of Votes: $numVotes")},
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            actions = {
                IconButton(onClick = {
                    onNavigateToResults()
                }) {
                    Icon(Icons.Filled.CheckCircle, null)
                }
            }
        )
        if (restaurants.isNullOrEmpty()) {
            Text(text = "No restaurants found")
        }
        else {
            LazyColumn(
                modifier = Modifier.padding(10.dp)
            ) {
                items(restaurants!!) {
                    restaurantCard(restaurant = it, userModel = userModel, numVotes)
                }
            }
        }
    }
}

@Composable
fun restaurantCard(
    restaurant: Businesse,
    userModel: UserModel,
    numVotes: Int) {
    var isClicked by rememberSaveable {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .background(if (isClicked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background)
    ) {
        Card( modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable() {
                                if (!isClicked && numVotes > 0) {
                                    userModel.voteForRestaurant(restaurant.name)
                                    isClicked = !isClicked
                                }
                                else if (isClicked) {
                                    userModel.removeRestaurantVote(restaurant.name)
                                    isClicked = !isClicked
                                }
                            }
        ) {
            Text(
                text = AnnotatedString("${restaurant.name}"),
                modifier = Modifier.padding(10.dp).fillMaxWidth())
        }
    }

}
