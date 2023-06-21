package com.mihaiim.sisgesjetpackcompose.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mihaiim.sisgesjetpackcompose.others.Constants.KEY_USER
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_LOGIN_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_REGISTER_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.ui.screens.LoginScreen
import com.mihaiim.sisgesjetpackcompose.ui.screens.RegisterScreen
import com.mihaiim.sisgesjetpackcompose.ui.theme.SisGesJetpackComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalPermissionsApi
@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(sharedPref.contains(KEY_USER)) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return
        }

        setContent {
            SisGesJetpackComposeTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "login_screen",
                ) {
                    composable(NAV_LOGIN_SCREEN_ROUTE) {
                        LoginScreen(navController = navController)
                    }
                    composable(NAV_REGISTER_SCREEN_ROUTE) {
                        RegisterScreen(navController = navController)
                    }
                }
            }
        }
    }
}
