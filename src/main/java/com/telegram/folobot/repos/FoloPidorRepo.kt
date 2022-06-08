package com.telegram.folobot.repos

import com.telegram.folobot.domain.FoloPidorEntity
import com.telegram.folobot.domain.FoloPidorId
import org.springframework.data.domain.Sort
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FoloPidorRepo : CrudRepository<FoloPidorEntity, FoloPidorId> {
    fun findAll(sort: Sort): List<FoloPidorEntity>
    fun findByIdChatId(chatId: Long): List<FoloPidorEntity>
    fun findByIdChatId(chatId: Long, sort: Sort): List<FoloPidorEntity>
}