package hu.ait.restauright.screen.home_screen

import android.Manifest
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.restauright.screen.login.HomeScreenViewModel
import hu.ait.restauright.screen.login.HomeUiState
import kotlinx.coroutines.launch
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import hu.ait.restauright.location.LocationManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToRestaurants: (String, String, String) -> Unit,
    homeScreenViewModel: HomeScreenViewModel = viewModel()
) {
    var userText by rememberSaveable {
        mutableStateOf("")
    }

    var showCreateSessionForm by rememberSaveable {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    if (showCreateSessionForm) {

        CreateNewSessionForm(onNavigateToRestaurants = onNavigateToRestaurants)
    } else {
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
                        if (result != null) {
                            onNavigateToRestaurants(result.zipCode, result.code, result.id)
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
                showCreateSessionForm = true
            }) {
                Text(text = "Create a new group")
            }

            Spacer(modifier = modifier.height(30.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (homeScreenViewModel.homeUiState) {
                    is HomeUiState.Loading -> CircularProgressIndicator()
                    is HomeUiState.Error -> Text(
                        text = "Error: ${
                            (homeScreenViewModel.homeUiState as HomeUiState.Error).error
                        }"
                    )

                    HomeUiState.Init -> {}
                    else -> {}
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewSessionForm(
    homeScreenViewModel: HomeScreenViewModel = viewModel(),
    onDialogDismiss: () -> Unit = {},
    onNavigateToRestaurants: (String, String, String) -> Unit
) {
    Dialog(onDismissRequest = onDialogDismiss) {
        var showLocationRequest by rememberSaveable {
            mutableStateOf(false)
        }
        var zipCode by rememberSaveable {
            mutableStateOf("")
        }

        Column(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Icon",
                Modifier.clickable {
                    showLocationRequest = true
                })
            if (showLocationRequest){
                getUserLocation()
            }
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "OR",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(50.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                value = zipCode,
                onValueChange = {
                    if (!it.isNullOrBlank()) {
                        zipCode = it
                    }
                },
                label = { Text(text = "Zip Code") }
            )

            Button(onClick = {
                homeScreenViewModel.createSession(zipCode) {result ->
                    var sessionCode: String
                    if (result != null) {
                        onNavigateToRestaurants(zipCode, result.code, result.id)
                    }
                }

            }) {
                Text(text = "Create Session")
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun getUserLocation(
    locationViewModel: LocationViewModel = hiltViewModel()

) {
    Column {
        val fineLocationPermissionState = rememberPermissionState(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (fineLocationPermissionState.status.isGranted) {
        Column {
            locationViewModel.startLocationMonitoring()
            Text(
                text = "Location: ${getLocationText(locationViewModel.locationState.value)}"
            )
        }

    } else {
        Column() {
            val permissionText = if (fineLocationPermissionState.status.shouldShowRationale) {
                "Please consider giving permission"
            } else {
                "Give permission for location"
            }
            Text(text = permissionText)
            Button(onClick = {
                fineLocationPermissionState.launchPermissionRequest()
            }) {
                Text(text = "Request permission")
            }
        }
    }
    }
}

fun getLocationText(location: Location?): String {
    return """
       Lat: ${location?.latitude}
       Lng: ${location?.longitude}
       Alt: ${location?.altitude}
       Speed: ${location?.speed}
       Accuracy: ${location?.accuracy}
    """.trimIndent()
}
