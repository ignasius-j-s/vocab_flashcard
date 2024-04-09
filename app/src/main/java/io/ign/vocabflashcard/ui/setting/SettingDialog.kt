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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.ign.vocabflashcard.R

@Composable
fun SettingDialog() {
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
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
                    RadioButton(selected = false, onClick = { /*TODO*/ })
                    Text("Name")
                    RadioButton(selected = false, onClick = { /*TODO*/ })
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
                    Checkbox(checked = true, onCheckedChange = {})
                }
                Divider()
            }
        },
        confirmButton = {
            Text(
                stringResource(R.string.ok_btn),
                modifier = Modifier.clickable { }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingDialog() {
    MaterialTheme {
        SettingDialog()
    }
}