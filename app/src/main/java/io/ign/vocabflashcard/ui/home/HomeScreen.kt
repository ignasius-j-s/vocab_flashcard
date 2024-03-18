package io.ign.vocabflashcard.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ign.vocabflashcard.R
import io.ign.vocabflashcard.data.Flashcard
import io.ign.vocabflashcard.ui.AppViewModelProvider
import io.ign.vocabflashcard.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@Composable
fun HomeScreen(
    navigateToFlashcardEntry: () -> Unit,
    navigateToFlashcardUpdate: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.flashcard_title_add)
                )
            }
        }
    ) {
        FlashcardList(flashcardList = homeUiState.flashcardList, onFlashcardClick = {})
    }

    NewFlashcardDialog(
        showDialog,
        onDismissRequest = { showDialog = false },
        onOkClick = { flashcard ->
            coroutineScope.launch { viewModel.saveFlashcard(flashcard) }
        }
    )
}

@Composable
fun HomeBody(
    flashcardList: List<Flashcard>,
    onFlashcardClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (flashcardList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_flashcard_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            FlashcardList(
                flashcardList = flashcardList,
                onFlashcardClick = { onFlashcardClick(it.id) }
            )
        }
    }
}

@Composable
fun FlashcardList(
    flashcardList: List<Flashcard>,
    onFlashcardClick: (Flashcard) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = flashcardList, key = { it.id }) { flashcard ->
            FlashcardEntry(
                flashcard = flashcard,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .clickable { onFlashcardClick(flashcard) }

            )
        }
    }
}

@Composable
fun FlashcardEntry(flashcard: Flashcard, modifier: Modifier = Modifier) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_small)
            )
        ) {
            Text(text = flashcard.name, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun NewFlashcardDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onOkClick: (Flashcard) -> Unit
) {
    var enableOkBtn by remember { mutableStateOf(false) }
    var flashcardName by remember { mutableStateOf("") }
    val onDismissRequest = {
        flashcardName = ""
        enableOkBtn = false
        onDismissRequest()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismissRequest()
            },
            confirmButton = {
                Button(
                    onClick = {
                        val flashcard = Flashcard(name = flashcardName)
                        onOkClick(flashcard)
                        onDismissRequest()
                    },
                    enabled = enableOkBtn
                ) {
                    Text(stringResource(R.string.ok_btn))
                }
            },
            dismissButton = {
                Button(onClick = { onDismissRequest() }) {
                    Text(stringResource(R.string.cancel_btn))
                }
            },
            title = {
                Text(stringResource(R.string.new_flashcard_dialog_title))
            },
            text = {
                Column {
                    Text(stringResource(R.string.new_flashcard_dialog_body))
                    Spacer(modifier = Modifier.padding(3.dp))
                    TextField(
                        value = flashcardName,
                        onValueChange = {
                            if (it.isNotBlank()) {
                                enableOkBtn = true
                            } else {
                                enableOkBtn = false
                            }

                            flashcardName = it
                        },
                        singleLine = true
                    )
                }
            }
        )
    }
}

//@Preview(showBackground = true)
@Composable
fun PreviewFlashcardList() {
    val flashcardList = listOf(
        Flashcard(0, "English"),
        Flashcard(1, "French"),
        Flashcard(2, "Japanese"),
        Flashcard(3, "Indonesia"),
    )
    FlashcardList(flashcardList = flashcardList, onFlashcardClick = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewNewFlashcardDialog() {
    NewFlashcardDialog(true, {}, {})
}