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
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import hu.ait.restauright.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToRestaurants: (String, String, String) -> Unit,
    onNavigateToRestaurantsByCoord: (Double, Double, String, String) -> Unit,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    var userText by rememberSaveable {
        mutableStateOf("")
    }

    var showCreateSessionForm by rememberSaveable {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    if (showCreateSessionForm) {

        CreateNewSessionForm(
            onNavigateToRestaurants = onNavigateToRestaurants,
            onNavigateToRestaurantsByCoord = onNavigateToRestaurantsByCoord
        )
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
                            if (result.zipCode == "null") {
                                onNavigateToRestaurantsByCoord(result.lat, result.lon, result.code, result.id)
                            }
                            else {
                                onNavigateToRestaurants(result.zipCode, result.code, result.id)

                            }
                        }
                    }
                }
            }) {
                Text(text = stringResource(R.string.join_a_group_with_a_code))
            }
            Spacer(modifier = modifier.height(50.dp))
            Text(
                text = stringResource(R.string.or),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = modifier.height(50.dp))
            Button(onClick = {
                showCreateSessionForm = true
            }) {
                Text(text = stringResource(R.string.create_a_new_group))
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
                        text = stringResource(
                            R.string.home_error,
                            (homeScreenViewModel.homeUiState as HomeUiState.Error).error!!
                        )
                    )

                    HomeUiState.Init -> {}
                    else -> {}
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CreateNewSessionForm(
    homeScreenViewModel: HomeScreenViewModel = viewModel(),
    locationViewModel: LocationViewModel = hiltViewModel(),
    onDialogDismiss: () -> Unit = {},
    onNavigateToRestaurants: (String, String, String) -> Unit,
    onNavigateToRestaurantsByCoord: (Double, Double, String, String) -> Unit
) {
    Dialog(onDismissRequest = onDialogDismiss) {
        var showLocationRequest by rememberSaveable {
            mutableStateOf(false)
        }
        var zipCode by rememberSaveable {
            mutableStateOf("")
        }
        var lon by rememberSaveable {
            mutableDoubleStateOf(0.0)
        }
        var lat by rememberSaveable {
            mutableDoubleStateOf(0.0)
        }

        var buttonText by rememberSaveable {
            mutableStateOf("Click to see real-time location")
        }

        Column(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Icon",
                Modifier
                    .clickable {
                        showLocationRequest = true
                    }
                    .size(50.dp))

            if (showLocationRequest) {
                Column {
                    val fineLocationPermissionState = rememberPermissionState(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    if (fineLocationPermissionState.status.isGranted) {
                        Column {


                            Button(onClick = {
                                getLocationText(locationViewModel.locationState.value)
                                buttonText = "Click again to create session"
                                if (locationViewModel.locationState.value != null){
                                    lat = locationViewModel.locationState.value?.latitude!!
                                    lon = locationViewModel.locationState.value?.longitude!!
                                    homeScreenViewModel.createSessionByCoord(lat, lon) { result ->
                                    if (result != null) {
                                        onNavigateToRestaurantsByCoord(lat, lon, result.code, result.id)
                                    }
                                }
                            }}){
                                Text(text = "$buttonText")
                            }
                            locationViewModel.startLocationMonitoring()
                            Text(
                                text = stringResource(
                                    R.string.loc,
                                    getLocationText(locationViewModel.locationState.value)
                                )
                            )

                        }

                    } else {
                        Column() {
                            val permissionText =
                                if (fineLocationPermissionState.status.shouldShowRationale) {
                                    stringResource(R.string.consider_giving_permission)
                                } else {
                                    stringResource(R.string.give_permission_loc)
                                }
                            Text(text = permissionText)
                            Button(onClick = {
                                fineLocationPermissionState.launchPermissionRequest()
                            }) {
                                Text(text = stringResource(R.string.request_permission_loc))
                            }
                        }
                    }
                }
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
                label = { Text(text = stringResource(R.string.zip_code)) }
            )

            Button(onClick = {
                homeScreenViewModel.createSession(zipCode) { result ->
                    if (result != null) {
                        onNavigateToRestaurants(zipCode, result.code, result.id)
                    }
                }

            }) {
                Text(text = stringResource(R.string.create_session))
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
                text = stringResource(
                    R.string.location,
                    getLocationText(locationViewModel.locationState.value)
                )
            )
        }

    } else {
        Column() {
            val permissionText = if (fineLocationPermissionState.status.shouldShowRationale) {
                stringResource(R.string.please_consider_giving_permission)
            } else {
                stringResource(R.string.give_permission_for_location)
            }
            Text(text = permissionText)
            Button(onClick = {
                fineLocationPermissionState.launchPermissionRequest()
            }) {
                Text(text = stringResource(R.string.request_permission))
            }
        }
    }
    }
}

fun getLocationText(location: Location?): String {
    return """
       Lat: ${location?.latitude}
       Lng: ${location?.longitude}
       Accuracy: ${location?.accuracy}
    """.trimIndent()
}
