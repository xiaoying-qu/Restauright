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
import hu.ait.restauright.screen.ResultsScreen
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
    startDestination: String = "sign_in",
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
               onNavigateToRestaurants = {location, sessionCode, sessionId ->
                   navController.navigate("display_restaurants/$location/$sessionCode/$sessionId")
               }
           )
        }

        composable("display_restaurants/{location}/{sessionCode}/{sessionId}",
            arguments = listOf(
                navArgument("location"){type = NavType.StringType},
                navArgument("sessionCode"){type = NavType.StringType},
                navArgument("sessionId"){type = NavType.StringType},
            )) {
            val location = it.arguments?.getString("location")
            val sessionCode = it.arguments?.getString("sessionCode")
            val sessionId = it.arguments?.getString("sessionId")
            if (location != null && sessionCode != null && sessionId != null) {
                DisplayRestaurantsScreen(
                    onNavigateToResults = { ->
                        navController.navigate("results")
                    },
                    sessionZipCode = location,
                    sessionCode = sessionCode,
                    sessionId = sessionId
                )
            }
        }

        composable("results") { ResultsScreen() }
    }
}
