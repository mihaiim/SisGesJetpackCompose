package com.mihaiim.sisgesjetpackcompose.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihaiim.sisgesjetpackcompose.R
import com.mihaiim.sisgesjetpackcompose.domain.model.Administration
import com.mihaiim.sisgesjetpackcompose.others.AdministrationEnum
import com.mihaiim.sisgesjetpackcompose.others.Constants.DATE_TIME_PATTERN
import com.mihaiim.sisgesjetpackcompose.ui.PrimaryButton
import com.mihaiim.sisgesjetpackcompose.ui.PrimaryTextField
import com.mihaiim.sisgesjetpackcompose.ui.SecondaryButton
import com.mihaiim.sisgesjetpackcompose.ui.SingleRadioButton
import com.mihaiim.sisgesjetpackcompose.ui.theme.Dimensions
import com.mihaiim.sisgesjetpackcompose.ui.theme.GradientBackground
import com.mihaiim.sisgesjetpackcompose.ui.theme.LightGrey
import com.mihaiim.sisgesjetpackcompose.ui.viewmodels.AdministrationViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AdministrationScreen(
    viewModel: AdministrationViewModel = hiltViewModel(),
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background,
    ) {
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current

        val administrationList by remember { viewModel.administrationList }
        val administrationType by remember { viewModel.administrationType }
        val filterVisible by remember { viewModel.filterVisible }
        val productCode by remember { viewModel.productCode }
        val productName by remember { viewModel.productName }
        val startDate by remember { viewModel.startDate }
        val endDate by remember { viewModel.endDate }

        val verticalScrollState = rememberScrollState()
        val horizontalScrollState = rememberScrollState()

        SetObservables(viewModel, context)

        Column(modifier = Modifier
            .fillMaxSize()
            .background(GradientBackground),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier
                .padding(start = Dimensions.defaultMargin)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { viewModel.toggleFilterVisible() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.filters),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = if (filterVisible)
                        R.drawable.ic_arrow_down else R.drawable.ic_arrow_up),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
            if (filterVisible) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = Dimensions.defaultMargin),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                    ) {
                        SingleRadioButton(
                            text = stringResource(id = R.string.in_and_out),
                            selected = administrationType == AdministrationEnum.ADD_AND_RETRIEVE,
                            onClick = {
                                viewModel.administrationType.value =
                                    AdministrationEnum.ADD_AND_RETRIEVE
                            },
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        SingleRadioButton(
                            text = stringResource(id = R.string.`in`),
                            selected = administrationType == AdministrationEnum.ADD,
                            onClick = {
                                viewModel.administrationType.value = AdministrationEnum.ADD
                            },
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        SingleRadioButton(
                            text = stringResource(id = R.string.out),
                            selected = administrationType == AdministrationEnum.RETRIEVE,
                            onClick = {
                                viewModel.administrationType.value = AdministrationEnum.RETRIEVE
                            },
                        )
                    }
                    PrimaryTextField(
                        text = productCode,
                        valueChanged = { viewModel.productCode.value = it },
                        modifier = Modifier
                            .fillMaxWidth(),
                        height = 32.dp,
                        hint = stringResource(R.string.product_code),
                        textSize = 11.sp,
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                        }),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Number,
                        ),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    PrimaryTextField(
                        text = productName,
                        valueChanged = { viewModel.productName.value = it },
                        modifier = Modifier
                            .fillMaxWidth(),
                        height = 32.dp,
                        hint = stringResource(R.string.product_name),
                        textSize = 11.sp,
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                        }),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text,
                        ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.from),
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(White, RoundedCornerShape(9.dp))
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    showDateTimePicker(
                                        context,
                                        startDate
                                    ) { year, month, day, hour, minute ->
                                        viewModel.setStartDate(
                                            year,
                                            month,
                                            day,
                                            hour,
                                            minute
                                        )
                                    }
                                },
                        ) {
                            Text(
                                text = startDate.format(DateTimeFormatter.ofPattern(
                                    DATE_TIME_PATTERN)),
                                color = Color.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.to),
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier
                            .background(White, RoundedCornerShape(9.dp))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                showDateTimePicker(
                                    context,
                                    endDate
                                ) { year, month, day, hour, minute ->
                                    viewModel.setEndDate(
                                        year,
                                        month,
                                        day,
                                        hour,
                                        minute
                                    )
                                }
                            },
                        ) {
                            Text(
                                text = endDate.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)),
                                color = Color.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        PrimaryButton(
                            text = stringResource(R.string.apply_filters),
                            onClick = { viewModel.getAdministration() },
                            height = 32.dp,
                            textSize = 11.sp,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SecondaryButton(
                            text = stringResource(R.string.reset_filters),
                            onClick = { viewModel.resetFilters() },
                            height = 32.dp,
                            textSize = 11.sp,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimensions.defaultMargin))
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState),
            ) {
                AdministrationHeader()
                administrationList.forEachIndexed { index, item ->
                    AdministrationItem(administration = item, index = index)
                }
            }
        }
    }
}

@Composable
fun AdministrationHeader() {
    Box(modifier = Modifier
        .height(34.dp)
        .background(White)) {
        Row(modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(28.dp))
            Text(
                text = stringResource(R.string.code),
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.width(86.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.name),
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.width(120.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.quantity_short),
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.width(56.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.date_and_time),
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.width(120.dp),
            )
        }
    }
}

@Composable
fun AdministrationItem(administration: Administration, index: Int) {
    Box(modifier = Modifier.background(if (index % 2 == 0) LightGrey else White)) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = if (administration.type == AdministrationEnum.ADD)
                    R.drawable.ic_arrow_in else R.drawable.ic_arrow_out),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = administration.productCode,
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.width(86.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = administration.productName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.width(120.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.pieces_short, administration.quantity),
                textAlign = TextAlign.End,
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.width(56.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = administration.getFormattedDateTime(),
                textAlign = TextAlign.End,
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.width(120.dp),
            )
        }
    }
}

@Composable
private fun SetObservables(
    viewModel: AdministrationViewModel,
    context: Context,
) {
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

private fun showDateTimePicker(
    context: Context,
    date: LocalDateTime,
    onSelect: (year: Int, month: Int, day: Int, hour: Int, minute: Int) -> Unit,
) {
    DatePickerDialog(
        context,
        { _, year, month, day ->
            TimePickerDialog(
                context,
                { _, hour, minute -> onSelect(year, month + 1, day, hour, minute) },
                date.hour,
                date.minute,
                true
            ).show()
        },
        date.year,
        date.monthValue - 1,
        date.dayOfMonth,
    ).show()
}