package com.mihaiim.sisgesjetpackcompose.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mihaiim.sisgesjetpackcompose.R
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_ADMINISTRATION_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_SCAN_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.others.ScanScreenTypeEnum
import com.mihaiim.sisgesjetpackcompose.ui.PrimaryButton
import com.mihaiim.sisgesjetpackcompose.ui.theme.Dimensions
import com.mihaiim.sisgesjetpackcompose.ui.theme.GradientBackground
import com.mihaiim.sisgesjetpackcompose.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background,
    ) {
        val context = LocalContext.current
        val user by remember { viewModel.user }

        SetObservables(viewModel = viewModel, context = context)

        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(GradientBackground),
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.hello_user, user.firstName),
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
            )
            Spacer(modifier = Modifier.height(12.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.photoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "${user.firstName} ${user.lastName}",
                placeholder = painterResource(id = R.drawable.ic_default_profile_picture),
                error = painterResource(id = R.drawable.ic_default_profile_picture),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.height(24.dp))
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (productsIn, productsOut, moveProducts,
                    seeStocks, seeInOut) = createRefs()
                val barrierStart = createStartBarrier(moveProducts)
                val barrierEnd = createEndBarrier(moveProducts)

                PrimaryButton(
                    text = stringResource(R.string.products_in),
                    onClick = { navController.navigate(
                        "$NAV_SCAN_SCREEN_ROUTE/${ScanScreenTypeEnum.IN.value}"
                    )},
                    modifier = Modifier
                        .constrainAs(productsIn) {
                            top.linkTo(parent.top)
                            start.linkTo(barrierStart)
                            end.linkTo(barrierEnd)
                            width = Dimension.fillToConstraints
                        },
                    textHorizontalPadding = 26.dp,
                )
                PrimaryButton(
                    text = stringResource(R.string.products_out),
                    onClick = { navController.navigate(
                        "$NAV_SCAN_SCREEN_ROUTE/${ScanScreenTypeEnum.OUT.value}"
                    )},
                    modifier = Modifier
                        .constrainAs(productsOut) {
                            top.linkTo(
                                anchor = productsIn.bottom,
                                margin = Dimensions.defaultMargin
                            )
                            start.linkTo(barrierStart)
                            end.linkTo(barrierEnd)
                            width = Dimension.fillToConstraints
                        },
                    textHorizontalPadding = 26.dp,
                )
                PrimaryButton(
                    text = stringResource(R.string.move_products),
                    onClick = { navController.navigate(
                        "$NAV_SCAN_SCREEN_ROUTE/${ScanScreenTypeEnum.MOVE.value}"
                    )},
                    modifier = Modifier
                        .constrainAs(moveProducts) {
                            top.linkTo(
                                anchor = productsOut.bottom,
                                margin = Dimensions.defaultMargin
                            )
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    textHorizontalPadding = 26.dp,
                )
                PrimaryButton(
                    text = stringResource(R.string.see_stocks),
                    onClick = { navController.navigate(
                        "$NAV_SCAN_SCREEN_ROUTE/${ScanScreenTypeEnum.SEE_STOCKS.value}"
                    )},
                    modifier = Modifier
                        .constrainAs(seeStocks) {
                            top.linkTo(
                                anchor = moveProducts.bottom,
                                margin = Dimensions.defaultMargin
                            )
                            start.linkTo(barrierStart)
                            end.linkTo(barrierEnd)
                            width = Dimension.fillToConstraints
                        },
                    textHorizontalPadding = 26.dp,
                )
                PrimaryButton(
                    text = stringResource(R.string.see_in_out),
                    onClick = { navController.navigate(NAV_ADMINISTRATION_SCREEN_ROUTE) },
                    modifier = Modifier
                        .constrainAs(seeInOut) {
                            top.linkTo(
                                anchor = seeStocks.bottom,
                                margin = Dimensions.defaultMargin
                            )
                            start.linkTo(barrierStart)
                            end.linkTo(barrierEnd)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.fillToConstraints
                        },
                    textHorizontalPadding = 26.dp,
                )
            }
            Spacer(modifier = Modifier.height(Dimensions.defaultMargin))
        }
    }
}

@Composable
private fun SetObservables(
    viewModel: HomeViewModel,
    context: Context,
) {
    LaunchedEffect(Unit) {
        viewModel.failFlow.collect {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}