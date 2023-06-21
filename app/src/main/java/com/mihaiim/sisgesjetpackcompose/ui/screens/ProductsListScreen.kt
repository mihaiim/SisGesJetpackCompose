package com.mihaiim.sisgesjetpackcompose.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mihaiim.sisgesjetpackcompose.R
import com.mihaiim.sisgesjetpackcompose.domain.model.Product
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_ARG_PRODUCT_NAME
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_PRODUCT_DETAILS_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.ui.theme.Dimensions
import com.mihaiim.sisgesjetpackcompose.ui.theme.GradientBackground
import com.mihaiim.sisgesjetpackcompose.ui.theme.Grey
import com.mihaiim.sisgesjetpackcompose.ui.viewmodels.ProductViewModel

@Composable
fun ProductsListScreen(
    searchTerm: String,
    navController: NavController,
    viewModel: ProductViewModel = hiltViewModel(),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background,
    ) {
        val context = LocalContext.current
        val productsList by remember { viewModel.productsList }

        SetObservables(searchTerm, viewModel, context, navController)

        Column(modifier = Modifier
            .fillMaxSize()
            .background(GradientBackground),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.choose_product),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = Dimensions.defaultMargin),
            ) {
                Text(
                    text = stringResource(id = R.string.code),
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(id = R.string.name),
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 74.dp),
                )
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .height(1.dp)
                .background(Grey),
            )
            LazyColumn {
                items(productsList) { product ->
                    ProductItem(
                        item = product,
                        onItemClicked = {
                            navController.navigate(
                                "${NAV_PRODUCT_DETAILS_SCREEN_ROUTE}/" +
                                        "${product.code}?$NAV_ARG_PRODUCT_NAME=${product.name}"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    item: Product,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onItemClicked() },
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.defaultMargin, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.code,
                color = Color.Black,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.width(Dimensions.defaultMargin))
            Text(
                text = item.name,
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.weight(1F)
            )
            Spacer(modifier = Modifier.width(Dimensions.defaultMargin))
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Grey),
        )
    }
}

@Composable
private fun SetObservables(
    searchTerm: String,
    viewModel: ProductViewModel,
    context: Context,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        viewModel.getProductsList(searchTerm)
    }
    LaunchedEffect(Unit) {
        viewModel.failFlow.collect {
            navController.popBackStack()
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}