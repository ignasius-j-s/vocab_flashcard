package io.ign.vocabflashcard.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ign.vocabflashcard.R
import io.ign.vocabflashcard.ui.AppViewModelProvider
import kotlinx.coroutines.launch


@Composable
fun SettingDialog(
    onDismissRequest: () -> Unit,
    viewModel: SettingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val settingUiState by viewModel.settingUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Settings") },
        text = {
            Column {
                Divider()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Sort",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(vertical = dimensionResource(R.dimen.padding_medium))
                            .weight(1f)
                    )
                    RadioButton(
                        selected = settingUiState.sortOrder == SortOrder.NAME,
                        onClick = { coroutineScope.launch { viewModel.saveSortOrder(SortOrder.NAME) } }
                    )
                    Text("Name")
                    RadioButton(
                        selected = settingUiState.sortOrder == SortOrder.TIME,
                        onClick = { coroutineScope.launch { viewModel.saveSortOrder(SortOrder.TIME) } }
                    )
                    Text("Time")
                }
                Divider()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Descending",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(vertical = dimensionResource(R.dimen.padding_medium))
                            .weight(1f)
                    )
                    Checkbox(
                        checked = settingUiState.descending,
                        onCheckedChange = {
                            coroutineScope.launch { viewModel.saveDescending(!settingUiState.descending) }
                        }
                    )
                }
                Divider()
            }
        },
        confirmButton = {
            Text(
                stringResource(R.string.ok_btn),
                modifier = Modifier.clickable { onDismissRequest() }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingDialog() {
    MaterialTheme {
        SettingDialog({})
    }
}