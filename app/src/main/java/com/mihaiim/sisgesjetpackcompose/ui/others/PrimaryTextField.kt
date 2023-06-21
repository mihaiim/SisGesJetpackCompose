package com.mihaiim.sisgesjetpackcompose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mihaiim.sisgesjetpackcompose.ui.theme.Dimensions

@Composable
fun PrimaryTextField(
    text: String,
    valueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    hint: String = "",
    textSize: TextUnit = 14.sp,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onFocusChanged: ((FocusState) -> Unit)? = null,
    isPassword: Boolean = false,
) {
    val insideTopPadding: Float = if (height.value - 20 >= 0) {
        (height.value - 20) / 2
    } else 0f
    Box(modifier = modifier
        .height(height)
        .border(
            1.dp,
            color = Color.Black,
            shape = RoundedCornerShape(12.dp),
        )
        .background(Color.White, RoundedCornerShape(12.dp)),
    ) {
        BasicTextField(
            value = text,
            onValueChange = {
                valueChanged(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = textSize,
            ),
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            visualTransformation = if (isPassword)
                PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(top = insideTopPadding.dp, start = 14.dp, end = 14.dp)
                .onFocusChanged { onFocusChanged?.invoke(it) },
        )
        if (hint.isNotEmpty() && text.isEmpty()) {
            Text(
                text = hint,
                color = Color.LightGray,
                fontSize = textSize,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = insideTopPadding.dp, start = 14.dp, end = 14.dp),
            )
        }
    }
}

@Preview
@Composable
fun PrimaryTextFieldPreview() {
    Column {
        PrimaryTextField(
            text = "PrimaryTextField",
            valueChanged = {},
            hint = "Hint",
        )
        Spacer(modifier = Modifier.height(Dimensions.defaultMargin))
        PrimaryTextField(
            text = "",
            valueChanged = {},
            hint = "Hint",
        )
        Spacer(modifier = Modifier.height(Dimensions.defaultMargin))
        PrimaryTextField(
            text = "Password",
            valueChanged = {},
            hint = "Hint",
            isPassword = true,
        )
    }
}