package io.ign.vocabflashcard.data

import kotlinx.coroutines.flow.Flow

interface GroupsRepository {
    suspend fun insertGroup(group: Group)
    suspend fun updateGroup(group: Group)
    suspend fun deleteGroup(group: Group)
    fun getAllGroupsStream(): Flow<List<Group>>
    fun getGroupStream(id: Int): Flow<Group?>
    fun getGroupWithCardsStream(id: Int): Flow<GroupWithCardsList?>
}