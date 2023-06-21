package com.mihaiim.sisgesjetpackcompose.ui.screens

import com.mihaiim.sisgesjetpackcompose.R
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihaiim.sisgesjetpackcompose.ui.theme.Dimensions
import com.mihaiim.sisgesjetpackcompose.ui.theme.GradientBackground
import com.mihaiim.sisgesjetpackcompose.ui.viewmodels.ProductViewModel

@Composable
fun ProductDetailsScreen(
    productCode: String,
    productName: String,
    viewModel: ProductViewModel = hiltViewModel(),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background,
    ) {
        val context = LocalContext.current
        val productDetails by remember { viewModel.productDetails }

        SetObservables(productCode, viewModel, context)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GradientBackground),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = productName.ifEmpty { productDetails.name },
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
            )
            Spacer(modifier = Modifier.height(24.dp))
            TitleText(
                title = stringResource(id = R.string.code_colon),
                text = productDetails.code,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
            )
            Spacer(modifier = Modifier.height(12.dp))
            TitleText(
                title = stringResource(id = R.string.total_quantity_colon),
                text = productDetails.getTotalQuantity().toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.storage_positions_colon),
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
            )
            productDetails.positions?.let { positions ->
                LazyColumn {
                    items(positions) { position ->
                        ProductPositionItem(item = position.getProductPositionString(context))
                    }
                }
            }
        }
    }
}

@Composable
fun TitleText(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.weight(1F),
        )
    }
}

@Composable
private fun SetObservables(
    productCode: String,
    viewModel: ProductViewModel,
    context: Context,
) {
    LaunchedEffect(Unit) {
        viewModel.getProductDetails(productCode)
    }
    LaunchedEffect(Unit) {
        viewModel.failFlow.collect {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}