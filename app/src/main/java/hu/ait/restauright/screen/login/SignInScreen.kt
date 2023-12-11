package hu.ait.restauright.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.restauright.R
import hu.ait.restauright.screen.login.LoginScreenViewModel
import hu.ait.restauright.screen.login.LoginUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    loginViewModel: LoginScreenViewModel = viewModel(),
    onNavigateToHomeScreen: () -> Unit
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("peter@ait.hu") }
    var password by rememberSaveable { mutableStateOf("123456") }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_font),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.8f)

        )
        Spacer(modifier = Modifier.size(24.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.8f),
            label = {
                Text(text = stringResource(R.string.e_mail))
            },
            value = email,
            onValueChange = {
                email = it
            },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Email, null)
            }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.8f),
            label = {
                Text(text = stringResource(R.string.password))
            },
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            visualTransformation =
            if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = {
                coroutineScope.launch {
                    val result = loginViewModel.loginUser(email, password)
                    if (result?.user != null) {
                        onNavigateToHomeScreen()
                    }
                }
            }) {
                Text(text = stringResource(R.string.login))
            }
            OutlinedButton(onClick = {
                loginViewModel.registerUser(email, password)
            }) {
                Text(text = stringResource(R.string.register))
            }
        }


        // Loading and error states
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (loginViewModel.loginUiState) {
                is LoginUiState.Loading -> CircularProgressIndicator()
                is LoginUiState.RegisterSuccess -> Text(text = stringResource(R.string.registration_ok))
                is LoginUiState.Error -> Text(
                    text = stringResource(
                        R.string.error,
                        (loginViewModel.loginUiState as LoginUiState.Error).error!!
                    )
                )

                is LoginUiState.LoginSuccess -> Text(text = stringResource(R.string.login_ok))
                LoginUiState.Init -> {}
            }
        }
    }
}