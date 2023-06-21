package com.mihaiim.sisgesjetpackcompose.ui.screens

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.mihaiim.sisgesjetpackcompose.R
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_PRODUCTS_LIST_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_PRODUCT_DETAILS_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.others.Constants.NAV_PRODUCT_POSITIONS_SCREEN_ROUTE
import com.mihaiim.sisgesjetpackcompose.others.ScanScreenTypeEnum
import com.mihaiim.sisgesjetpackcompose.ui.PrimaryButton
import com.mihaiim.sisgesjetpackcompose.ui.PrimaryTextField
import com.mihaiim.sisgesjetpackcompose.ui.theme.Dimensions
import com.mihaiim.sisgesjetpackcompose.ui.theme.GradientBackground
import com.mihaiim.sisgesjetpackcompose.ui.theme.Grey70
import com.mihaiim.sisgesjetpackcompose.ui.viewmodels.ScanViewModel

@ExperimentalPermissionsApi
@Composable
fun ScanScreen(
    screenTypeParam: Int,
    navController: NavController,
    checkAndRequestCameraPermission: () -> PermissionStatus,
    viewModel: ScanViewModel = hiltViewModel(),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background,
    ) {
        val context = LocalContext.current
        val screenType by remember { viewModel.screenType }
        val step by remember { viewModel.step }
        val quantityLeft by remember { viewModel.quantityLeft }

        SetObservables(screenTypeParam, viewModel, context, navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GradientBackground),
        ) {
            if (screenType == ScanScreenTypeEnum.OUT && step == 1) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.products_out_information),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            viewModel.product?.let {
                                viewModel.scanInProgress.value = false
                                navController.navigate(
                                    "${NAV_PRODUCT_POSITIONS_SCREEN_ROUTE}/${it.code}/${it.name}")
                            }
                        }
                        .background(Grey70)
                        .padding(
                            horizontal = Dimensions.defaultMargin,
                            vertical = 8.dp,
                        ),
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            DescriptionText(
                screenType = screenType,
                step = step,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
            )
            Spacer(modifier = Modifier.height(16.dp))
            ScanBox(
                viewModel = viewModel,
                checkAndRequestCameraPermission = checkAndRequestCameraPermission,
                context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))
            Inputs(
                screenType = screenType,
                step = step,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            WarningText(
                screenType = screenType,
                step = step,
                quantity = quantityLeft,
                context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
            )
            Spacer(modifier = Modifier.height(24.dp))
            ContinueButton(
                screenType = screenType,
                step = step,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun DescriptionText(
    screenType: ScanScreenTypeEnum,
    step: Int,
    modifier: Modifier = Modifier,
) {
    val text = when (screenType) {
        ScanScreenTypeEnum.IN -> when (step) {
            0 -> stringResource(id = R.string.products_in_description)
            1 -> stringResource(id = R.string.products_in_shelf_description)
            else -> ""
        }
        ScanScreenTypeEnum.OUT -> when (step) {
            0 -> stringResource(id = R.string.products_out_description)
            1 -> stringResource(id = R.string.products_out_shelf_description)
            else -> ""
        }
        ScanScreenTypeEnum.MOVE -> stringResource(id = R.string.move_products_description)
        ScanScreenTypeEnum.SEE_STOCKS -> stringResource(id = R.string.see_stocks_description)
        ScanScreenTypeEnum.NONE -> ""
    }
    Text(
        text = text,
        color = Color.Black,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

@ExperimentalPermissionsApi
@Composable
fun ScanBox(
    viewModel: ScanViewModel,
    checkAndRequestCameraPermission: () -> PermissionStatus,
    context: Context,
    modifier: Modifier = Modifier,
) {
    val scanInProgress by remember { viewModel.scanInProgress }

    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                val barcodeView = View.inflate(
                    context,
                    R.layout.view_bracode_reader,
                    null,
                ) as DecoratedBarcodeView
                barcodeView.setStatusText("")
                barcodeView.decodeContinuous {
                    viewModel.handleScanResult(it.text)
                }
                barcodeView
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                if (scanInProgress) {
                    view.resume()
                    return@AndroidView
                }
                view.pauseAndWait()
            }
        )
        if (!scanInProgress) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        when (val permissionStatus = checkAndRequestCameraPermission()) {
                            is PermissionStatus.Granted -> {
                                viewModel.scanInProgress.value = true
                            }
                            is PermissionStatus.Denied -> {
                                if (!permissionStatus.shouldShowRationale) {
                                    Toast
                                        .makeText(
                                            context,
                                            R.string.camera_permission_denied_message_scan,
                                            Toast.LENGTH_LONG
                                        )
                                        .show()
                                }
                            }
                        }
                    },
            ) {
                Text(
                    text = stringResource(id = R.string.press_to_scan),
                    color = Color.White,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun Inputs(
    screenType: ScanScreenTypeEnum,
    step: Int,
    viewModel: ScanViewModel,
    modifier: Modifier = Modifier,
) {
    val input1 by remember { viewModel.input1 }
    val input2 by remember { viewModel.input2 }
    val input3 by remember { viewModel.input3 }
    val input4 by remember { viewModel.input4 }
    val scrollState = rememberScrollState()

    Column(modifier = modifier.verticalScroll(scrollState)) {
        Input1TextField(
            screenType = screenType,
            step = step,
            text = input1,
            valueChanged = { viewModel.input1.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.defaultMargin),
            onFocusChanged = {
                if (it.hasFocus) {
                    viewModel.changeInputsFocus(input1HasFocus = true)
                }
            },
        )
        if (screenType != ScanScreenTypeEnum.SEE_STOCKS) {
            Spacer(modifier = Modifier.height(16.dp))
            Input2TextField(
                screenType = screenType,
                step = step,
                text = input2,
                valueChanged = { viewModel.input2.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.defaultMargin),
                onFocusChanged = {
                    if (it.hasFocus) {
                        viewModel.changeInputsFocus(input2HasFocus = true)
                    }
                },
            )
        }
        if (screenType == ScanScreenTypeEnum.MOVE) {
            if (step >= 1) {
                Spacer(modifier = Modifier.height(16.dp))
                Input3TextField(
                    text = input3,
                    valueChanged = { viewModel.input3.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimensions.defaultMargin),
                    onFocusChanged = {
                        if (it.hasFocus) {
                            viewModel.changeInputsFocus(input3HasFocus = true)
                        }
                    },
                )
            }
            if (step == 2) {
                Spacer(modifier = Modifier.height(16.dp))
                Input4TextField(
                    text = input4,
                    valueChanged = { viewModel.input4.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimensions.defaultMargin),
                    onFocusChanged = {
                        if (it.hasFocus) {
                            viewModel.changeInputsFocus(input4HasFocus = true)
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun Input1TextField(
    screenType: ScanScreenTypeEnum,
    step: Int,
    text: String,
    valueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFocusChanged: ((FocusState) -> Unit),
) {
    val focusManager = LocalFocusManager.current
    val hint = when (screenType) {
        ScanScreenTypeEnum.IN,
        ScanScreenTypeEnum.OUT -> when (step) {
            0 -> stringResource(id = R.string.product_code)
            1 -> stringResource(id = R.string.shelf_code)
            else -> ""
        }
        ScanScreenTypeEnum.MOVE -> stringResource(id = R.string.product_code)
        ScanScreenTypeEnum.SEE_STOCKS -> stringResource(id = R.string.product_code_or_name)
        ScanScreenTypeEnum.NONE -> ""
    }
    val keyboardActions = if (screenType == ScanScreenTypeEnum.SEE_STOCKS)
        KeyboardActions { focusManager.clearFocus() } else KeyboardActions.Default
    val keyboardType = when (screenType) {
        ScanScreenTypeEnum.IN,
        ScanScreenTypeEnum.OUT -> when (step) {
            0 -> KeyboardType.Number
            else -> KeyboardType.Text
        }
        ScanScreenTypeEnum.MOVE -> KeyboardType.Number
        ScanScreenTypeEnum.SEE_STOCKS -> KeyboardType.Text
        ScanScreenTypeEnum.NONE -> KeyboardType.Text
    }
    val capitalization = when (screenType) {
        ScanScreenTypeEnum.IN,
        ScanScreenTypeEnum.OUT -> when (step) {
            0 -> KeyboardCapitalization.None
            else -> KeyboardCapitalization.Characters
        }
        ScanScreenTypeEnum.MOVE -> KeyboardCapitalization.None
        ScanScreenTypeEnum.SEE_STOCKS -> KeyboardCapitalization.Sentences
        ScanScreenTypeEnum.NONE -> KeyboardCapitalization.None
    }
    PrimaryTextField(
        text = text,
        valueChanged = valueChanged,
        modifier = modifier,
        hint = hint,
        keyboardActions = keyboardActions,
        keyboardOptions = KeyboardOptions(
            imeAction = if (screenType == ScanScreenTypeEnum.SEE_STOCKS)
                ImeAction.Done else ImeAction.Next,
            keyboardType = keyboardType,
            capitalization = capitalization,
        ),
        onFocusChanged = onFocusChanged,
    )
}

@Composable
fun Input2TextField(
    screenType: ScanScreenTypeEnum,
    step: Int,
    text: String,
    valueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFocusChanged: ((FocusState) -> Unit),
) {
    val focusManager = LocalFocusManager.current
    val keyboardActions = if (screenType != ScanScreenTypeEnum.MOVE)
        KeyboardActions { focusManager.clearFocus() } else KeyboardActions.Default
    PrimaryTextField(
        text = text,
        valueChanged = valueChanged,
        modifier = modifier,
        hint = stringResource(id = if (screenType == ScanScreenTypeEnum.MOVE)
            R.string.current_shelf_code else R.string.quantity),
        keyboardActions = keyboardActions,
        keyboardOptions = KeyboardOptions(
            imeAction = if (screenType == ScanScreenTypeEnum.MOVE && step >= 1)
                ImeAction.Next else ImeAction.Done,
            keyboardType = if (screenType == ScanScreenTypeEnum.MOVE)
                KeyboardType.Text else KeyboardType.Number,
            capitalization = if (screenType == ScanScreenTypeEnum.MOVE)
                KeyboardCapitalization.Characters else KeyboardCapitalization.None,
        ),
        onFocusChanged = onFocusChanged,
    )
}

@Composable
fun Input3TextField(
    text: String,
    valueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFocusChanged: ((FocusState) -> Unit),
) {
    PrimaryTextField(
        text = text,
        valueChanged = valueChanged,
        modifier = modifier,
        hint = stringResource(id = R.string.quantity),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number,
            capitalization = KeyboardCapitalization.None,
        ),
        onFocusChanged = onFocusChanged,
    )
}

@Composable
fun Input4TextField(
    text: String,
    valueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFocusChanged: ((FocusState) -> Unit),
) {
    val focusManager = LocalFocusManager.current
    PrimaryTextField(
        text = text,
        valueChanged = valueChanged,
        modifier = modifier,
        hint = stringResource(id = R.string.new_shelf_code),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Characters,
        ),
        onFocusChanged = onFocusChanged,
    )
}

@Composable
fun WarningText(
    screenType: ScanScreenTypeEnum,
    step: Int,
    quantity: Int,
    context: Context,
    modifier: Modifier = Modifier,
) {
    if (!((screenType == ScanScreenTypeEnum.IN && step == 1) ||
                (screenType == ScanScreenTypeEnum.OUT && step == 1))) {
        return
    }

    val text = if (screenType == ScanScreenTypeEnum.IN) {
        context.resources.getQuantityString(
            R.plurals.products_in_left,
            quantity,
            quantity,
        )
    } else {
        context.resources.getQuantityString(
            R.plurals.products_out_left,
            quantity,
            quantity,
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = text,
        color = Color.Red,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

@Composable
fun ContinueButton(
    screenType: ScanScreenTypeEnum,
    step: Int,
    viewModel: ScanViewModel,
    modifier: Modifier = Modifier,
) {
    val text = if (screenType == ScanScreenTypeEnum.IN) {
        if (step == 0) {
            stringResource(id = R.string.next)
        } else {
            stringResource(id = R.string.deposit)
        }
    } else if (screenType == ScanScreenTypeEnum.OUT) {
        if (step == 0) {
            stringResource(id = R.string.next)
        } else {
            stringResource(id = R.string.collect)
        }
    } else if (screenType == ScanScreenTypeEnum.MOVE) {
        if (step == 2) {
            stringResource(id = R.string.finish)
        } else {
            stringResource(id = R.string.next)
        }
    } else {
        stringResource(id = R.string.see_stock)
    }
    PrimaryButton(
        text = text,
        onClick = { viewModel.goForward() },
        modifier = modifier,
    )
}

@Composable
private fun SetObservables(
    screenTypeParam: Int,
    viewModel: ScanViewModel,
    context: Context,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        viewModel.screenType.value = ScanScreenTypeEnum.values()[screenTypeParam]
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
    LaunchedEffect(Unit) {
        viewModel.navigateBackFlow.collect {
            navController.popBackStack()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.navigateToProductsListFlow.collect {
            navController.navigate("${NAV_PRODUCTS_LIST_SCREEN_ROUTE}/$it")
        }
    }
    LaunchedEffect(Unit) {
        viewModel.navigateToProductDetailsFlow.collect {
            navController.navigate("${NAV_PRODUCT_DETAILS_SCREEN_ROUTE}/$it")
        }
    }
}