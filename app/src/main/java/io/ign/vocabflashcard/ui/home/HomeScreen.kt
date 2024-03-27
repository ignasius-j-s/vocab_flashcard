package io.ign.vocabflashcard.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    var showNewDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedFlashcard by remember { mutableStateOf(Flashcard(name = "")) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewDialog = true },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_flashcard_title)
                )
            }
        }
    ) { innerPadding ->
        HomeBody(
            flashcardList = homeUiState.flashcardList,
            onFlashcardClick = {},
            onDelete = { flashcard ->
                showDeleteDialog = true
                selectedFlashcard = flashcard
            },
            onEdit = { flashcard ->
                showEditDialog = true
                selectedFlashcard = flashcard
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }

    NewFlashcardDialog(
        showNewDialog,
        onDismiss = { showNewDialog = false },
        onOkClick = { flashcard ->
            coroutineScope.launch { viewModel.saveFlashcard(flashcard) }
        }
    )
    EditFlashcardDialog(
        showEditDialog,
        onDismiss = { showEditDialog = false },
        onOkClick = { flashcardName ->
            coroutineScope.launch { viewModel.editFlashcard(selectedFlashcard, flashcardName) }
        }
    )
    DeleteFlashcardDialog(
        showDeleteDialog,
        onDismiss = { showDeleteDialog = false },
        onOkClick = {
            coroutineScope.launch { viewModel.deleteFlashcard(selectedFlashcard) }
        }
    )
}

@Composable
fun HomeBody(
    flashcardList: List<Flashcard>,
    onFlashcardClick: (Int) -> Unit,
    onDelete: (Flashcard) -> Unit,
    onEdit: (Flashcard) -> Unit,
    modifier: Modifier = Modifier
) {
    if (flashcardList.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.no_flashcard_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
    } else {
        FlashcardList(
            flashcardList = flashcardList,
            onFlashcardClick = { onFlashcardClick(it.id) },
            onDelete = onDelete,
            onEdit = onEdit,
            modifier
        )
    }
}

@Composable
fun FlashcardList(
    flashcardList: List<Flashcard>,
    onFlashcardClick: (Flashcard) -> Unit,
    onDelete: (Flashcard) -> Unit,
    onEdit: (Flashcard) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = flashcardList, key = { it.id }) { flashcard ->
            FlashcardEntry(
                flashcard = flashcard,
                onDelete = onDelete,
                onEdit = onEdit,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .clickable { onFlashcardClick(flashcard) }
            )
        }
    }
}

@Composable
fun FlashcardEntry(
    flashcard: Flashcard,
    onDelete: (Flashcard) -> Unit,
    onEdit: (Flashcard) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_small)
            )
        ) {
            Text(text = flashcard.name, style = MaterialTheme.typography.titleLarge)
            Row {
                IconButton(onClick = {
                    // TODO: add alert dialog
                    onDelete(flashcard)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.delete_flashcard_title),
                    )
                }
                IconButton(onClick = { onEdit(flashcard) }) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.edit_flashcard_title),
                    )
                }
            }
        }
    }
}

@Composable
fun NewFlashcardDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onOkClick: (Flashcard) -> Unit
) {
    FlashcardDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
        onOkClick = { flashcardName ->
            val flashcard = Flashcard(name = flashcardName)
            onOkClick(flashcard)
        },
        title = R.string.new_flashcard_title,
        text = R.string.new_flashcard_text
    )
}

@Composable
fun EditFlashcardDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onOkClick: (String) -> Unit
) {
    FlashcardDialog(
        showDialog = showDialog,
        onDismiss = onDismiss,
        onOkClick = onOkClick,
        title = R.string.edit_flashcard_title,
        text = R.string.edit_flashcard_text
    )
}

@Composable
fun DeleteFlashcardDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onOkClick()
                        onDismiss()
                    },
                ) {
                    Text(stringResource(R.string.ok_btn))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel_btn))
                }
            },
            title = {
                Text(stringResource(R.string.delete_flashcard_title))
            },
            text = {
                Text(stringResource(R.string.delete_flashcard_text))
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = stringResource(R.string.delete_flashcard_title)
                )
            }
        )
    }
}

@Composable
fun FlashcardDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onOkClick: (String) -> Unit,
    @StringRes title: Int,
    @StringRes text: Int
) {
    var enableOkButton by remember { mutableStateOf(false) }
    var flashcardName by remember { mutableStateOf("") }
    val onDismissRequest = {
        flashcardName = ""
        enableOkButton = false
        onDismiss()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = {
                        onOkClick(flashcardName)
                        onDismissRequest()
                    },
                    enabled = enableOkButton
                ) {
                    Text(stringResource(R.string.ok_btn))
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(stringResource(R.string.cancel_btn))
                }
            },
            title = {
                Text(stringResource(title))
            },
            text = {
                Column {
                    Text(stringResource(text))
                    Spacer(modifier = Modifier.padding(3.dp))
                    TextField(
                        value = flashcardName,
                        onValueChange = {
                            enableOkButton = it.isNotBlank()
                            flashcardName = it
                        },
                        singleLine = true
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyFlashcard() {
    HomeBody(flashcardList = listOf(), onFlashcardClick = {}, onDelete = {}, onEdit = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewFlashcardList() {
    val flashcardList = listOf(
        Flashcard(0, "English"),
        Flashcard(1, "French"),
        Flashcard(2, "Japanese"),
        Flashcard(3, "Indonesia"),
    )
    FlashcardList(flashcardList = flashcardList, onFlashcardClick = {}, onDelete = {}, onEdit = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewNewFlashcardDialog() {
    NewFlashcardDialog(true, {}, {})
}