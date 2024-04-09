@file:OptIn(ExperimentalMaterial3Api::class)

package io.ign.vocabflashcard.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import io.ign.vocabflashcard.R

@Composable
fun TopBar(
    title: String = "Title",
    canNavigateBack: Boolean,
    onNavBackClick: () -> Unit = {},
    showSettingButton: Boolean = true,
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavBackClick) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "navBack",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        actions = {
            actions()
            if (showSettingButton) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "settings",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        modifier = Modifier.shadow(dimensionResource(R.dimen.shadow_small))
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTopBar() {
    TopBar(canNavigateBack = true)
}