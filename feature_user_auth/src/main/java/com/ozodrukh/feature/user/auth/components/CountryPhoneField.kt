package com.ozodrukh.feature.user.auth.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.util.Locale


/**
 * @param value Полный номер телефона (например, "+79991234567").
 * @param onValueChange Колбек с новым полным номером.
 * @param modifier Модификатор для стилизации.
 * @param useCurrentRegionAsDefault Если true, при пустом value попытается определить страну по SIM/System locale.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryPhoneInput(
    value: String,
    onValueChange: (String, String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    useCurrentRegionAsDefault: Boolean = true,
    label: @Composable (() -> Unit)? = { Text("Номер телефона") },
    isError: Boolean = false
) {
    val context = LocalContext.current


    val defaultCountry = remember {
        if (useCurrentRegionAsDefault) getCurrentRegionCountry(context) else CountryUtils.getAllCountries()
            .first()
    }

    var selectedCountry by remember { mutableStateOf(defaultCountry) }

    LaunchedEffect(value) {
        if (value.startsWith("+")) {
            val detected = CountryUtils.getCountryByPhoneCode(value)
            if (detected != null && detected.code != selectedCountry.code) {
                selectedCountry = detected
            }
        }
    }

    var showCountryDialog by remember { mutableStateOf(false) }

    fun checkValidity(digits: String, mask: String): Boolean {
        val requiredDigits = mask.count { it == '#' }
        return digits.length == requiredDigits
    }

    fun onNationalNumberChanged(text: String) {
        val digits = text.filter { it.isDigit() }
        val fullNumber = "${selectedCountry.phoneCode}$digits"
        val isValid = checkValidity(digits, selectedCountry.mask)
        onValueChange(selectedCountry.code, fullNumber, isValid)
    }

    val nationalNumber = if (value.startsWith(selectedCountry.phoneCode)) {
        value.removePrefix(selectedCountry.phoneCode)
    } else {
        ""
    }

    val placeholderText = selectedCountry.mask.replace("#", "0")

    Column(modifier = modifier) {
        OutlinedTextField(
            value = nationalNumber,
            onValueChange = { onNationalNumberChanged(it) },
            label = label,
            placeholder = {
                Text(
                    placeholderText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = isError,
            visualTransformation = PhoneVisualTransformation(selectedCountry.mask, '#'),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            leadingIcon = {
                Row(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        modifier = Modifier
                            .clickable { showCountryDialog = true }
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedCountry.flagEmoji,
                            fontSize = 24.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }


                    Text(
                        text = selectedCountry.phoneCode,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    VerticalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        )

        if (showCountryDialog) {
            CountrySelectionDialog(
                onDismissRequest = { showCountryDialog = false },
                onCountrySelected = { country ->
                    selectedCountry = country
                    val isValid = checkValidity(nationalNumber, country.mask)
                    onValueChange(country.code, country.phoneCode + nationalNumber, isValid)
                    showCountryDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountrySelectionDialog(
    onDismissRequest: () -> Unit,
    onCountrySelected: (CountryData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val allCountries = remember { CountryUtils.getAllCountries() }

    val filteredCountries = remember(searchQuery) {
        if (searchQuery.isEmpty()) allCountries
        else allCountries.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phoneCode.contains(searchQuery) ||
                    it.code.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Выберите страну",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Поиск...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredCountries) { country ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCountrySelected(country) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = country.flagEmoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = country.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = country.phoneCode,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}


data class CountryData(
    val code: String,
    val name: String,
    val phoneCode: String,
    val mask: String
) {
    val flagEmoji: String
        get() {
            val firstLetter = Character.codePointAt(code, 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(code, 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        }
}

object CountryUtils {

    fun getAllCountries(): List<CountryData> = listOf(
        CountryData("RU", "Russia", "+7", "(###) ###-##-##"),
        CountryData("KZ", "Kazakhstan", "+7", "(###) ###-##-##"),
        CountryData("UZ", "Uzbekistan", "+998", "## ### ## ##"),
        CountryData("US", "United States", "+1", "(###) ###-####"),
        CountryData("BY", "Belarus", "+375", "(##) ###-##-##"),
        CountryData("UA", "Ukraine", "+380", "(##) ###-##-##"),
        CountryData("GB", "United Kingdom", "+44", "#### ######"),
        CountryData("DE", "Germany", "+49", "#### #######"),
        CountryData("FR", "France", "+33", "# ## ## ## ##"),
        CountryData("IT", "Italy", "+39", "### #######"),
        CountryData("ES", "Spain", "+34", "### ### ###"),
        CountryData("TR", "Turkey", "+90", "(###) ### ## ##"),
        CountryData("AM", "Armenia", "+374", "## ######"),
        CountryData("GE", "Georgia", "+995", "### ## ## ##"),
    )

    fun getCountryByPhoneCode(phone: String): CountryData? {
        return getAllCountries()
            .sortedByDescending { it.phoneCode.length }
            .firstOrNull { phone.startsWith(it.phoneCode) }
    }

    fun getCountryByIso(isoCode: String): CountryData? {
        return getAllCountries().find { it.code.equals(isoCode, ignoreCase = true) }
    }
}

private fun getCurrentRegionCountry(context: Context): CountryData {
    val regionIso = context.resources.configuration.locales.get(0).country ?: "RU"
    return CountryUtils.getCountryByIso(regionIso) ?: CountryUtils.getAllCountries().first()
}


class PhoneVisualTransformation(
    private val mask: String,
    private val maskChar: Char = '#'
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text
        val annotatedString = AnnotatedString.Builder().run {
            var trimIndex = 0
            for (char in mask) {
                if (char == maskChar) {
                    if (trimIndex < trimmed.length) {
                        append(trimmed[trimIndex++])
                    } else break
                } else {
                    append(char)
                }
            }
            toAnnotatedString()
        }

        val translator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var noneDigitCount = 0
                var i = 0
                while (i < offset + noneDigitCount && i < mask.length) {
                    if (mask[i++] != maskChar) noneDigitCount++
                }
                return (offset + noneDigitCount).coerceAtMost(annotatedString.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                var noneDigitCount = 0
                var i = 0
                while (i < offset && i < mask.length) {
                    if (mask[i++] != maskChar) noneDigitCount++
                }
                return offset - noneDigitCount
            }
        }
        return TransformedText(annotatedString, translator)
    }
}