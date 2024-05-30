@file:OptIn(ExperimentalMaterial3Api::class)

package io.ign.vocabflashcard.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import io.ign.vocabflashcard.R
import io.ign.vocabflashcard.ui.setting.SettingDialog
import io.ign.vocabflashcard.ui.theme.VocabFlashcardTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String = "Title",
    canNavigateBack: Boolean,
    onNavBackClick: () -> Unit = {},
    showSettingButton: Boolean = true,
    actions: @Composable () -> Unit = {}
) {
    var showSettingDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavBackClick) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "navBack",
                    )
                }
            }
        },
        actions = {
            actions()
            if (showSettingButton) {
                IconButton(onClick = { showSettingDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "settings",
                    )
                }
            }
        },
        modifier = Modifier.shadow(dimensionResource(R.dimen.shadow_small))
    )

    if (showSettingDialog) {
        SettingDialog(onDismissRequest = { showSettingDialog = false })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTopBar() {
    VocabFlashcardTheme {
        TopBar(canNavigateBack = true)
    }
}