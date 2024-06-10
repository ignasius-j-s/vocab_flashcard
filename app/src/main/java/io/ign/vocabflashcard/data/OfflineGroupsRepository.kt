package io.ign.vocabflashcard.data

import io.ign.vocabflashcard.ui.setting.SortOrder
import kotlinx.coroutines.flow.Flow

class OfflineGroupsRepository(private val groupDao: GroupDao) : GroupsRepository {
    override suspend fun insertGroup(group: Group) = groupDao.insert(group)
    override suspend fun updateGroup(group: Group) = groupDao.update(group)
    override suspend fun deleteGroup(group: Group) = groupDao.delete(group)
    override fun getAllGroupsStream(
        sortOrder: SortOrder,
        isDescending: Boolean
    ): Flow<List<Group>> {
        return when (sortOrder) {
            SortOrder.NAME -> groupDao.getAllGroupsByName(isDescending)
            SortOrder.TIME -> groupDao.getAllGroupsByTime(isDescending)
        }
    }

    override fun getGroupStream(id: Int): Flow<Group?> = groupDao.getGroup(id)
    override fun getGroupWithCardsStream(id: Int): Flow<GroupWithCardsList?> =
        groupDao.getGroupWithCardsList(id)
}