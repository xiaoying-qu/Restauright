package hu.ait.restauright.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import hu.ait.restauright.Data.restaurant_result.RestaurantResult
import hu.ait.restauright.R
import hu.ait.restauright.components.CardStack

@Composable
fun DisplayRestaurantsScreen (
    restaurantsViewModel: RestaurantsViewModel = hiltViewModel(),
    onNavigateToResults: (String) -> Unit,
    sessionCode: String,
    sessionId: String,
    sessionZipCode: String
) {
    LaunchedEffect(key1 = Unit) {
        restaurantsViewModel.getRestaurants(sessionZipCode)
    }

    Column {
        when (restaurantsViewModel.restaurantUiState) {
            is RestaurantUiState.Init -> {}
            is RestaurantUiState.Loading -> CircularProgressIndicator()
            is RestaurantUiState.Success -> {
                restaurantsViewModel.addRestaurauntsToSession((restaurantsViewModel.restaurantUiState as RestaurantUiState.Success).Restaurant.businesses, sessionId)
                ResultScreen(
                    (restaurantsViewModel.restaurantUiState as RestaurantUiState.Success).Restaurant,
                    onNavigateToResults = onNavigateToResults,
                    sessionCode = sessionCode,
                    sessionId = sessionId
                )
            }
            is RestaurantUiState.Error -> Text(text = stringResource(
                R.string.error_display,
                (restaurantsViewModel.restaurantUiState as RestaurantUiState.Error).errorMsg
            ))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ResultScreen(
    restaurant: RestaurantResult,
    onNavigateToResults: (String) -> Unit,
    sessionCode: String,
    sessionId: String,
) {
    val restaurants by rememberSaveable {
        mutableStateOf(restaurant.businesses)
    }

    Column {
        TopAppBar(
            title = { Text(text = stringResource(R.string.code, sessionCode))},
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )
        if (restaurants.isNullOrEmpty()) {
            Text(text = stringResource(R.string.no_restaurants_found))
        }
        else {
            CardStack(items = restaurants!!, sessionId = sessionId, onNavigateToResults = onNavigateToResults)

        }
    }
}
