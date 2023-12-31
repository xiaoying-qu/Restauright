package hu.ait.restauright.screen.results

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.restauright.screen.login.ResultsViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import hu.ait.restauright.Data.Restaurant
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import coil.compose.rememberAsyncImagePainter
import hu.ait.restauright.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
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
            .fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = rememberAsyncImagePainter(R.drawable.logo),
                        contentDescription = "Logo Image",
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(end = 8.dp)
                            .size(45.dp)
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
        ) {
            items(restaurants) {
                RestaurantCard(restaurant = it)
            }
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = "${restaurant?.restaurant?.name}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${restaurant?.votes}")
            }
        }
    }
}