package hu.ait.restauright

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import hu.ait.restauright.screen.DisplayRestaurantsScreen
import hu.ait.restauright.screen.home_screen.HomeScreen
import hu.ait.restauright.screen.results.ResultsScreen
import hu.ait.restauright.screen.SignInScreen
import hu.ait.restauright.ui.theme.RestaurightTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RestaurightTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RestaurightNavHost()
                }
            }
        }
    }
}

@Composable
fun RestaurightNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "sign_in"
) {
    NavHost(
        modifier = modifier, navController = navController, startDestination = startDestination
    ) {

        composable("sign_in") {
            SignInScreen(
                onNavigateToHomeScreen = { ->
                    navController.navigate("home_screen")
                }
            )
        }


        composable("home_screen") {
            HomeScreen(
                onNavigateToRestaurants = { location, sessionCode, sessionId ->
                    navController.navigate("display_restaurants/$location/$sessionCode/$sessionId")
                },
                onNavigateToRestaurantsByCoord = {lat,lon,sessionCode,sessionId->
                    navController.navigate("display_restaurants/$lat/$lon/$sessionCode/$sessionId")

                })
        }

        composable(
            "display_restaurants/{lat}/{lon}/{sessionCode}/{sessionId}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType },
                navArgument("sessionCode") { type = NavType.StringType },
                navArgument("sessionId") { type = NavType.StringType },
            )
        ) {
            val lat = it.arguments?.getString("lat")
            val lon = it.arguments?.getString("lon")
            val sessionCode = it.arguments?.getString("sessionCode")
            val sessionId = it.arguments?.getString("sessionId")
            if (lat != null && lon != null && sessionCode != null && sessionId != null) {
                DisplayRestaurantsScreen(
                    onNavigateToResults = { sessionId ->
                        navController.navigate("results/$sessionId")
                    },
                    sessionCode = sessionCode,
                    sessionLat = lat,
                    sessionLon = lon,
                    sessionId = sessionId
                )
            }
        }

        composable("display_restaurants/{zipCode}/{sessionCode}/{sessionId}",
            arguments = listOf(
                navArgument("zipCode"){type = NavType.StringType},
            )) {
            val zipCode = it.arguments?.getString("zipCode")
            val sessionCode = it.arguments?.getString("sessionCode")
            val sessionId = it.arguments?.getString("sessionId")
            if (zipCode != null && sessionCode != null && sessionId != null) {
                DisplayRestaurantsScreen(
                    onNavigateToResults = {sessionId ->
                        navController.navigate("results/$sessionId")
                    },
                    sessionCode = sessionCode,
                    sessionId = sessionId,
                    sessionZipCode = zipCode,
                )
            }
        }


        composable(
            "results/{sessionId}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
            )
        ) {
            val sessionId = it.arguments?.getString("sessionId")
            if (sessionId != null) {
                ResultsScreen(
                    sessionId = sessionId
                )
            }
        }
    }
}
