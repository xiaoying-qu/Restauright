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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.ait.restauright.screen.DisplayRestaurantsScreen
import hu.ait.restauright.screen.HomeScreen
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
               onNavigateToRestaurants = {->
                   navController.navigate("display_restaurants")
               }
           )
        }

        composable("display_restaurants") {
            DisplayRestaurantsScreen(
                onNavigateToResults = {->
                    navController.navigate("results")
                }
            )
        }

        composable("results") { ResultsScreen() }
    }
}
