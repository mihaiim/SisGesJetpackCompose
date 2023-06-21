package com.mihaiim.sisgesjetpackcompose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mihaiim.sisgesjetpackcompose.ui.theme.Parsley

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 54.dp,
    textSize: TextUnit = 14.sp,
    textHorizontalPadding: Dp = 0.dp,
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
            .height(height)
            .background(Parsley, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = textHorizontalPadding),
        )
    }
}

@Preview
@Composable
fun PrimaryButtonPreview() {
    PrimaryButton(
        text = "PrimaryButton",
        onClick = {},
    )
}