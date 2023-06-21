package com.mihaiim.sisgesjetpackcompose.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mihaiim.sisgesjetpackcompose.ui.theme.Dimensions

@Composable
fun SingleRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            null,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.White,
                unselectedColor = Color.White,
            ),
        )
        Text(
            text = text,
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 3.dp),
        )
    }
}

@Preview
@Composable
fun PrimarySingleRadioButton() {
    Column {
        SingleRadioButton(
            text = "SingleRadioButton",
            selected = true,
            onClick = {},
        )
        Spacer(modifier = Modifier.height(Dimensions.defaultMargin))
        SingleRadioButton(
            text = "SingleRadioButton",
            selected = false,
            onClick = {},
        )
    }
}