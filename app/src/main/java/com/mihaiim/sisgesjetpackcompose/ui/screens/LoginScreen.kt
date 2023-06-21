package com.mihaiim.sisgesjetpackcompose.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mihaiim.sisgesjetpackcompose.R
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_REGISTER_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.ui.PrimaryButton
import com.mihaiim.sisgesjetpackcompose.ui.PrimaryTextField
import com.mihaiim.sisgesjetpackcompose.ui.SecondaryButton
import com.mihaiim.sisgesjetpackcompose.ui.activities.HomeActivity
import com.mihaiim.sisgesjetpackcompose.ui.theme.Dimensions
import com.mihaiim.sisgesjetpackcompose.ui.theme.GradientBackground
import com.mihaiim.sisgesjetpackcompose.ui.viewmodels.LoginViewModel

@ExperimentalPermissionsApi
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background,
    ) {
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current

        val email by remember { viewModel.email }
        val password by remember { viewModel.password }
        val scrollState = rememberScrollState()

        SetObservables(viewModel, context)

        Column(modifier = Modifier
            .fillMaxSize()
            .background(GradientBackground)
            .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
            )
            Text(
                text = stringResource(R.string.app_name),
                color = Color.Black,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = Dimensions.defaultMargin),
            )
            Text(
                text = stringResource(R.string.app_name_subtitle),
                color = Color.Black,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = Dimensions.defaultMargin),
            )
            Spacer(modifier = Modifier.height(60.dp))
            PrimaryTextField(
                text = email,
                valueChanged = { viewModel.email.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
                hint = stringResource(R.string.email),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryTextField(
                text = password,
                valueChanged = { viewModel.password.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
                hint = stringResource(R.string.password),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password,
                ),
                isPassword = true,
            )
            Spacer(modifier = Modifier.height(40.dp))
            PrimaryButton(
                text = stringResource(R.string.login),
                onClick = {
                    viewModel.loginUser()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
            )
            Spacer(modifier = Modifier.height(16.dp))
            SecondaryButton(
                text = stringResource(R.string.register),
                onClick = {
                    navController.navigate(NAV_REGISTER_SCREEN_ROUTE)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
            )
        }
    }
}

@ExperimentalPermissionsApi
@Composable
private fun SetObservables(viewModel: LoginViewModel, context: Context) {
    LaunchedEffect(Unit) {
        viewModel.successFlow.collect {
            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.failFlow.collect {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.errorResIdFlow.collect {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}