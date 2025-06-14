package io.ign.vocabflashcard.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnderlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val state by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        textStyle = TextStyle.Default.copy(
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        interactionSource = interactionSource,
        modifier = modifier.indicatorLine(
            enabled = state,
            isError = false,
            interactionSource = interactionSource,
            colors = TextFieldDefaults.colors(),
            focusedIndicatorLineThickness = TextFieldDefaults.FocusedIndicatorThickness,
            unfocusedIndicatorLineThickness = TextFieldDefaults.UnfocusedIndicatorThickness,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimaryContainer)
    )
}
