package com.mihaiim.sisgesjetpackcompose.ui.activities

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.mihaiim.sisgesjetpackcompose.R
import com.mihaiim.sisgesjetpackcompose.domain.repository.UserRepository
import com.mihaiim.sisgesjetpackcompose.others.Constants.KEY_USER
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_ADMINISTRATION_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_ARG_PRODUCT_CODE
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_ARG_PRODUCT_NAME
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_ARG_SCREEN_TYPE
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_ARG_SEARCH_TERM
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_HOME_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_PRODUCTS_LIST_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_PRODUCT_DETAILS_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_PRODUCT_POSITIONS_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_PROFILE_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_SCAN_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.ui.model.MenuItem
import com.mihaiim.sisgesjetpackcompose.ui.screens.*
import com.mihaiim.sisgesjetpackcompose.ui.theme.DarkSeaGreen
import com.mihaiim.sisgesjetpackcompose.ui.theme.Parsley
import com.mihaiim.sisgesjetpackcompose.ui.theme.SisGesJetpackComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPermissionsApi
@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SisGesJetpackComposeTheme {
                val scaffoldState = rememberScaffoldState()
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val cameraPermissionState = rememberPermissionState(
                    permission = Manifest.permission.CAMERA
                )

                BackPressHandler(onBackPressed = {
                    if (scaffoldState.drawerState.isOpen) {
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                    } else {
                        super.onBackPressed()
                    }
                })

                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = {
                        AppBar(
                            onNavigationIconClick = {
                                scope.launch {
                                    scaffoldState.drawerState.open()
                                }
                            }
                        )
                    },
                    drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
                    drawerContent = {
                        DrawerHeader()
                        DrawerBody(
                            items = listOf(
                                MenuItem(
                                    id = 0,
                                    title = stringResource(id = R.string.home),
                                    iconResId = R.drawable.ic_home,
                                ),
                                MenuItem(
                                    id = 1,
                                    title = stringResource(id = R.string.profile),
                                    iconResId = R.drawable.ic_profile,
                                )
                            ),
                            onItemClick = { item ->
                                if (item.id == 0) {
                                    navController.navigate(NAV_HOME_SCREEN_ROUTE)
                                } else {
                                    navController.navigate(NAV_PROFILE_SCREEN_ROUTE)
                                }
                                scope.launch {
                                    scaffoldState.drawerState.close()
                                }
                            },
                            onLogOut = {
                                scope.launch {
                                    userRepository.logout()
                                    sharedPref.edit().remove(KEY_USER).apply()
                                    val intent = Intent(
                                        this@HomeActivity,
                                        LoginActivity::class.java
                                    )
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                            Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }
                            },
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = NAV_HOME_SCREEN_ROUTE,
                        modifier = Modifier.padding(paddingValues),
                    ) {
                        composable(route = NAV_HOME_SCREEN_ROUTE) {
                            HomeScreen(navController = navController)
                        }

                        composable(
                            route = "$NAV_SCAN_SCREEN_ROUTE/{$NAV_ARG_SCREEN_TYPE}",
                            arguments = listOf(
                                navArgument(NAV_ARG_SCREEN_TYPE) {
                                    type = NavType.IntType
                                }
                            )) {
                            ScanScreen(
                                screenTypeParam = it.arguments?.getInt(NAV_ARG_SCREEN_TYPE) ?: 0,
                                navController = navController,
                                checkAndRequestCameraPermission = {
                                    checkAndRequestCameraPermission(cameraPermissionState)
                                },
                            )
                        }

                        composable(
                            route = "$NAV_PRODUCT_POSITIONS_SCREEN_ROUTE/" +
                                    "{$NAV_ARG_PRODUCT_CODE}/{$NAV_ARG_PRODUCT_NAME}",
                            arguments = listOf(
                                navArgument(NAV_ARG_PRODUCT_CODE) {
                                    type = NavType.StringType
                                },
                                navArgument(NAV_ARG_PRODUCT_NAME) {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            ProductPositionsScreen(
                                productCode = it.arguments?.getString(NAV_ARG_PRODUCT_CODE) ?: "",
                                productName = it.arguments?.getString(NAV_ARG_PRODUCT_NAME) ?: "",
                            )
                        }

                        composable(
                            route = "$NAV_PRODUCTS_LIST_SCREEN_ROUTE/{$NAV_ARG_SEARCH_TERM}",
                            arguments = listOf(
                                navArgument(NAV_ARG_SEARCH_TERM) {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            ProductsListScreen(
                                searchTerm = it.arguments?.getString(NAV_ARG_SEARCH_TERM) ?: "",
                                navController = navController,
                            )
                        }

                        composable(
                            route = "$NAV_PRODUCT_DETAILS_SCREEN_ROUTE/{$NAV_ARG_PRODUCT_CODE}" +
                                    "?$NAV_ARG_PRODUCT_NAME={$NAV_ARG_PRODUCT_NAME}",
                            arguments = listOf(
                                navArgument(NAV_ARG_PRODUCT_CODE) {
                                    type = NavType.StringType
                                },
                                navArgument(NAV_ARG_PRODUCT_NAME) {
                                    nullable = true
                                }
                            )
                        ) {
                            ProductDetailsScreen(
                                productCode = it.arguments?.getString(NAV_ARG_PRODUCT_CODE) ?: "",
                                productName = it.arguments?.getString(NAV_ARG_PRODUCT_NAME) ?: "",
                            )
                        }

                        composable(route = NAV_ADMINISTRATION_SCREEN_ROUTE) {
                            AdministrationScreen()
                        }

                        composable(route = NAV_PROFILE_SCREEN_ROUTE) {
                            ProfileScreen(
                                navController = navController,
                                checkAndRequestCameraPermission = {
                                    checkAndRequestCameraPermission(cameraPermissionState)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackPressHandler(
    backPressedDispatcher: OnBackPressedDispatcher? =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
    onBackPressed: () -> Unit,
) {
    val currentOnBackPressed by rememberUpdatedState(newValue = onBackPressed)
    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }
    DisposableEffect(key1 = backPressedDispatcher) {
        backPressedDispatcher?.addCallback(backCallback)
        onDispose { backCallback.remove() }
    }
}

@Composable
fun AppBar(onNavigationIconClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 70.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp),
                )
            }
        },
        backgroundColor = DarkSeaGreen,
        contentColor = Color.Black,
        elevation = 0.dp,
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle drawer",
                )
            }
        },
    )
}

@Composable
fun DrawerHeader() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Parsley)
        .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp),
            )
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.app_name_subtitle),
                    color = Color.White,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
fun DrawerBody(
    items: List<MenuItem>,
    onItemClick: (MenuItem) -> Unit,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(items) { item ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(16.dp),
                ) {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.title,
                        tint = Color.Black,
                        modifier = Modifier
                            .size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = item.title,
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.weight(1f),
                        color = Color.Black,
                    )
                }
            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onLogOut() },
        ) {
            Text(
                text = stringResource(id = R.string.logout),
                color = Color.Red,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            )
        }
    }
}

@ExperimentalPermissionsApi
private fun checkAndRequestCameraPermission(permissionState: PermissionState): PermissionStatus {
    if (permissionState.status is PermissionStatus.Denied) {
        permissionState.launchPermissionRequest()
    }
    return permissionState.status
}