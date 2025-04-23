@file:OptIn(ExperimentalMaterial3Api::class)

package io.ign.vocabflashcard.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import io.ign.vocabflashcard.R
import io.ign.vocabflashcard.ui.setting.SettingDialog

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
                        Icons.AutoMirrored.Filled.ArrowBack,
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
        modifier = Modifier.shadow(dimensionResource(R.dimen.top_app_shadow))
    )

    if (showSettingDialog) {
        SettingDialog(onDismissRequest = { showSettingDialog = false })
    }
}
