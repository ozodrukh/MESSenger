package com.ozodrukh.feature.user.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    otpLength: Int,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 8.dp,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Hidden TextField for input
        BasicTextField(
            value = value,
            onValueChange = {
                if (it.length <= otpLength && it.all { char -> char.isDigit() }) {
                    onValueChange(it)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            cursorBrush = SolidColor(Color.Transparent),
            decorationBox = { },
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
        )

        // Visual representation
        Row(
            horizontalArrangement = Arrangement.spacedBy(horizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(otpLength) { i ->
                val char = value.getOrNull(i)
                OtpBox(
                    char = char,
                    isError = isError,
                    isFocused = i == value.length
                )
            }
        }
    }
}

@Composable
fun OtpBox(
    char: Char?,
    isError: Boolean,
    isFocused: Boolean,
    modifier: Modifier = Modifier,
) {
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.primary
        else -> Color.LightGray
    }

    val borderWidth = if (isFocused || isError) 2.dp else 1.dp

    Box(
        modifier = modifier
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .width(43.dp)
            .height(52.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = char?.toString() ?: "",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Composable
@Preview
fun OtpBoxPreview() {
    OtpTextField(
        value = "123",
        onValueChange = {},
        isError = false,
        otpLength = 6
    )
}