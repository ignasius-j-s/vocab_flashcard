package io.ign.vocabflashcard.ui.group

import io.ign.vocabflashcard.data.Group

data class GroupUiState(
    val group: Group = Group(0, ""),
    val isEntryValid: Boolean = false,
)

fun Group.toGroupUiState(isEntryValid: Boolean): GroupUiState {
    return GroupUiState(this, isEntryValid)
}