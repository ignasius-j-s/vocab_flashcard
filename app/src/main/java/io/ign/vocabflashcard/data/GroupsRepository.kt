package io.ign.vocabflashcard.data

import io.ign.vocabflashcard.ui.setting.SortOrder
import kotlinx.coroutines.flow.Flow

interface GroupsRepository {
    suspend fun insertGroup(group: Group)
    suspend fun updateGroup(group: Group)
    suspend fun deleteGroup(group: Group)
    fun getAllGroupsStream(sortOrder: SortOrder, isDescending: Boolean): Flow<List<Group>>
    fun getGroupStream(id: Int): Flow<Group?>
    fun getGroupWithCardsStream(id: Int): Flow<GroupWithCardsList?>
}