package com.ozodrukh.feature.user.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OtpTextField(
    text: MutableState<String>,
    position: MutableIntState,
    errorState: State<Boolean>,
    onValueChange: (text: String) -> Unit,
    otpLength: Int,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 8.dp,
) {
    LaunchedEffect(text.value) {
        if (text.value.length > otpLength) {
            text.value = text.value.take(otpLength)
            position.intValue = position.intValue.coerceAtMost(otpLength)
        } else onValueChange(text.value)
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(horizontalPadding),
    ) {
        items(otpLength) { i ->
            val char = remember(text.value) {
                mutableStateOf(text.value.getOrNull(i))
            }

            OtpBox(
                char = char,
                isError = errorState,
            )
        }
    }
}

@Composable
fun OtpBox(
    char: MutableState<Char?>,
    isError: State<Boolean>,
    modifier: Modifier = Modifier,
) {
    val borderModifier = remember(char.value, isError.value) {
        when {
            char.value != null && isError.value -> Modifier
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))

            else -> Modifier
                .border(0.dp, Color.Transparent, RoundedCornerShape(12.dp))
        }
    }

    Box(
        modifier = modifier
            .then(borderModifier)
            .background(Color.White, RoundedCornerShape(12.dp))
            .width(43.dp)
            .height(52.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "${char.value ?: '_'}",
            style = LocalTextStyle.current.copy(
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Composable
@Preview
fun OtpBoxPreview() {
    val text: MutableState<String> = remember {
        mutableStateOf("1234")
    }

    val position = remember { mutableIntStateOf(1) }
    val errorState = remember { mutableStateOf(true) }

    OtpTextField(
        text = text,
        position = position,
        errorState = errorState,
        otpLength = 6,
        onValueChange = {},
    )
}
