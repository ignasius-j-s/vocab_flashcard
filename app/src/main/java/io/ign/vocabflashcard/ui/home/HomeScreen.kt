package io.ign.vocabflashcard.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ign.vocabflashcard.R
import io.ign.vocabflashcard.data.Group
import io.ign.vocabflashcard.ui.AppViewModelProvider
import io.ign.vocabflashcard.ui.CustomTextField
import io.ign.vocabflashcard.ui.TopBar
import io.ign.vocabflashcard.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object HomeScreenDestination : NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    navigateToGroup: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    var showNewDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedGroup by remember { mutableStateOf(Group(name = "")) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(canNavigateBack = false, title = stringResource(R.string.app_name))
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.group_add_title)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.group_add_title)
                    )
                },
                onClick = { showNewDialog = true },
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
        }
    ) { innerPadding ->
        HomeBody(
            groupList = homeUiState.groupList,
            favFlashcardCount = 0, //TODO
            onGroupClick = { id -> navigateToGroup(id) },
            onDelete = { group ->
                selectedGroup = group
                showDeleteDialog = true
            },
            onEdit = { group ->
                selectedGroup = group
                showEditDialog = true
            },
            modifier = modifier
                .padding(innerPadding)
        )
    }

    if (showNewDialog) {
        NewGroupDialog(
            onDismiss = { showNewDialog = false },
            onOkClick = { group ->
                coroutineScope.launch { viewModel.saveGroup(group) }
            }
        )
    }

    if (showEditDialog) {
        EditGroupDialog(
            value = selectedGroup.name,
            onDismiss = { showEditDialog = false },
            onOkClick = { groupName ->
                coroutineScope.launch { viewModel.editGroup(selectedGroup, groupName) }
            }
        )
    }

    if (showDeleteDialog) {
        DeleteGroupDialog(
            onDismiss = { showDeleteDialog = false },
            onOkClick = {
                coroutineScope.launch { viewModel.deleteGroup(selectedGroup) }
            }
        )
    }
}

@Composable
fun HomeBody(
    groupList: List<Group>,
    favFlashcardCount: Int = 0,
    onGroupClick: (Int) -> Unit,
    onDelete: (Group) -> Unit,
    onEdit: (Group) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FavoriteEntry(
            onClick = { },
            favFlashcardCount = favFlashcardCount,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_extra_small))
        )
        Divider(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small)))
        if (groupList.isEmpty()) {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.group_nonexsistent_description),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            GroupList(
                groupList = groupList,
                onGroupClick = { onGroupClick(it.id) },
                onDelete = onDelete,
                onEdit = onEdit,
            )
        }
    }
}

@Composable
fun GroupList(
    groupList: List<Group>,
    onGroupClick: (Group) -> Unit,
    onDelete: (Group) -> Unit,
    onEdit: (Group) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LazyColumn(
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_extra_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_extra_small))
        ) {
            items(items = groupList, key = { it.id }) { group ->
                GroupEntry(
                    group = group,
                    onGroupClick = onGroupClick,
                    onDelete = onDelete,
                    onEdit = onEdit
                )
            }
        }
    }
}

@Composable
fun FavoriteEntry(
    onClick: () -> Unit,
    favFlashcardCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = stringResource(R.string.favorites),
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            )
            Text(
                text = stringResource(R.string.favorites),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
            )
            Text(
                text = favFlashcardCount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                modifier = Modifier
                    .alpha(0.8f)
                    .padding(horizontal = dimensionResource(R.dimen.padding_large))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupEntry(
    group: Group,
    onGroupClick: (Group) -> Unit,
    onDelete: (Group) -> Unit,
    onEdit: (Group) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onGroupClick(group) }
    ) {
        var moreMenu by remember { mutableStateOf(false) }

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_medium))
                    .weight(1f)
            )
            Box {
                IconButton(onClick = { moreMenu = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "more"
                    )
                }
                DropdownMenu(expanded = moreMenu, onDismissRequest = { moreMenu = false }) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = stringResource(R.string.group_edit_title)
                                )
                                Spacer(Modifier.padding(horizontal = dimensionResource(R.dimen.padding_extra_small)))
                                Text(
                                    stringResource(R.string.group_edit_title),
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        onClick = { onEdit(group); moreMenu = false }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = stringResource(R.string.group_delete_title),
                                    tint = Color(0xFFEF5350)
                                )
                                Spacer(Modifier.padding(horizontal = dimensionResource(R.dimen.padding_extra_small)))
                                Text(
                                    stringResource(R.string.group_delete_title),
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                        onClick = { onDelete(group); moreMenu = false }
                    )
                }
            }
        }
    }
}

@Composable
fun NewGroupDialog(
    onDismiss: () -> Unit,
    onOkClick: (Group) -> Unit
) {
    GroupDialog(
        onDismiss = onDismiss,
        onOkClick = { groupName ->
            val group = Group(name = groupName)
            onOkClick(group)
        },
        title = R.string.group_add_title,
    )
}

@Composable
fun EditGroupDialog(
    value: String,
    onDismiss: () -> Unit,
    onOkClick: (String) -> Unit
) {
    GroupDialog(
        value,
        onDismiss = onDismiss,
        onOkClick = onOkClick,
        title = R.string.group_edit_title,
    )
}

@Composable
fun DeleteGroupDialog(
    onDismiss: () -> Unit,
    onOkClick: () -> Unit
) {
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
            Text(stringResource(R.string.group_delete_title))
        },
        text = {
            Text(stringResource(R.string.group_delete_confirmation))
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = stringResource(R.string.group_delete_title),
                tint = Color(0xFFEF5350)
            )
        }
    )
}

@Composable
fun GroupDialog(
    value: String = "",
    onDismiss: () -> Unit,
    onOkClick: (String) -> Unit,
    @StringRes title: Int,
) {
    var enableOkButton by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf(value) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onOkClick(groupName)
                    onDismiss()
                },
                enabled = enableOkButton
            ) {
                Text(stringResource(R.string.ok_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel_btn))
            }
        },
        title = {
            Text(stringResource(title))
        },
        text = {
            Column {
                CustomTextField(
                    value = groupName,
                    onValueChange = {
                        enableOkButton = it.isNotBlank()
                        groupName = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEmptyGroup() {
    HomeBody(groupList = listOf(), onGroupClick = {}, onDelete = {}, onEdit = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewGroupList() {
    val groupList = listOf(
        Group(0, "English"),
        Group(1, "French"),
        Group(2, "Japanese"),
        Group(3, "Indonesia"),
    )
    GroupList(
        groupList = groupList,
        onGroupClick = {},
        onDelete = {},
        onEdit = {},
        modifier = Modifier.padding(
            dimensionResource(
                id = R.dimen.padding_small
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewNewGroupDialog() {
    NewGroupDialog({}, {})
}

@Preview(showBackground = true)
@Composable
fun PreviewDeleteGroupDialog() {
    DeleteGroupDialog({}, {})
}
