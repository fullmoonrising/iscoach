package com.telegram.folobot.persistence.repos

import com.telegram.folobot.persistence.entity.FoloPidorEntity
import com.telegram.folobot.persistence.entity.FoloPidorId
import org.springframework.data.domain.Sort
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FoloPidorRepo : CrudRepository<FoloPidorEntity, FoloPidorId> {
    fun findAll(sort: Sort): List<FoloPidorEntity>
    fun findFoloPidorById(id: FoloPidorId): FoloPidorEntity?
    fun findByIdChatId(chatId: Long): List<FoloPidorEntity>
    fun findByIdChatId(chatId: Long, sort: Sort): List<FoloPidorEntity>

    fun findFirstByIdChatIdOrderByMessagesPerDayDesc(chatId: Long): FoloPidorEntity?
}