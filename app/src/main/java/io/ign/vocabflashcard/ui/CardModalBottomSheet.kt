package io.ign.vocabflashcard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.ign.vocabflashcard.R
import io.ign.vocabflashcard.data.CardData
import io.ign.vocabflashcard.data.Usage
import io.ign.vocabflashcard.data.Translation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CardModalBottomSheet(
    data: CardData,
    onOkClick: (CardData, List<Translation>, List<Usage>) -> Unit,
    onDismiss: () -> Unit,
) {
    var cardTerm by remember { mutableStateOf(data.card.term) }
    var cardDescription by remember { mutableStateOf(data.card.description) }
    var cardNote by remember { mutableStateOf(data.card.note) }
    var translationList = remember { mutableStateListOf<Translation>() }
    var usageList = remember { mutableStateListOf<Usage>() }

    translationList.addAll(data.translationList)
    usageList.addAll(data.usageList)

    var translationDeleteList = remember { mutableStateListOf<Translation>() }
    var usageDeleteList = remember { mutableStateListOf<Usage>() }

    val enableOkButton = cardTerm.isNotBlank() && cardDescription.isNotBlank()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        val padding = dimensionResource(R.dimen.padding_extra_large)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = padding)
        ) {
            OutlinedButton(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) onDismiss()
                    }
                }
            ) { Text(stringResource(R.string.cancel)) }
            Button(
                enabled = enableOkButton,
                onClick = {
                    val card = data.card.copy(
                        term = cardTerm,
                        description = cardDescription,
                        note = cardNote
                    )
                    val cardData = CardData(card, translationList, usageList)
                    onOkClick(cardData, translationDeleteList, usageDeleteList)
                    onDismiss()
                }
            ) { Text(stringResource(R.string.save)) }
        }
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            OutlinedTextField(
                cardTerm,
                onValueChange = { cardTerm = it },
                singleLine = true,
                label = { Text(stringResource(R.string.card_term)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                cardDescription,
                onValueChange = { cardDescription = it },
                label = { Text(stringResource(R.string.card_description)) },
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                cardNote,
                onValueChange = { cardNote = it },
                label = { Text(stringResource(R.string.card_note)) },
                modifier = Modifier.fillMaxWidth(),
            )
            HorizontalDivider()
            Text(stringResource(R.string.translation), style = MaterialTheme.typography.labelMedium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            ) {
                var translationValue by remember { mutableStateOf("") }
                var translationDialog by remember { mutableStateOf(false) }
                translationList.forEach { tl ->
                    AssistChip(
                        onClick = {
                            translationList.remove(tl)
                            if (data.translationList.contains(tl)) translationDeleteList.add(tl)
                        },
                        label = {
                            Text(tl.translation, style = MaterialTheme.typography.labelMedium)
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(MaterialTheme.typography.labelMedium.fontSize.value.dp)
                            )
                        }
                    )
                }
                AssistChip(
                    onClick = { translationDialog = true },
                    label = {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.typography.labelMedium.fontSize.value.dp)
                        )
                    }
                )
                if (translationDialog) {
                    val onDismiss = {
                        translationValue = ""
                        translationDialog = false
                    }
                    AlertDialog(
                        shape = RoundedCornerShape(dimensionResource(R.dimen.round_radius)),
                        onDismissRequest = onDismiss,
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    translationList.add(
                                        Translation(
                                            translation = translationValue, cardId = data.card.id
                                        )
                                    )
                                    onDismiss()
                                },
                                contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
                                shape = RoundedCornerShape(dimensionResource(R.dimen.round_radius)),
                                enabled = translationValue.isNotBlank()
                            ) { Text(stringResource(R.string.ok)) }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = onDismiss,
                                contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
                                shape = RoundedCornerShape(dimensionResource(R.dimen.round_radius))
                            ) { Text(stringResource(R.string.cancel)) }
                        },
                        title = { Text(stringResource(R.string.translation_add)) },
                        text = {
                            UnderlinedTextField(
                                value = translationValue,
                                onValueChange = { translationValue = it },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }
            }
            HorizontalDivider()
            Text(stringResource(R.string.usage), style = MaterialTheme.typography.labelMedium)
            usageList.forEachIndexed { index, usg ->
                var value by remember { mutableStateOf(usg.usage) }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            value = it
                            usageList[index] = usg.copy(usage = value)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            usageList.remove(usg)
                            if (data.usageList.contains(usg)) usageDeleteList.add(usg)
                        }
                    ) { Icon(Icons.Outlined.Clear, null) }
                }
            }
            Button(
                onClick = { usageList.add(Usage(usage = "", cardId = data.card.id)) },
                shape = RoundedCornerShape(dimensionResource(R.dimen.round_radius)),
                modifier = Modifier.fillMaxWidth(),
            ) { Text(stringResource(R.string.usage_add)) }
        }
    }
}
