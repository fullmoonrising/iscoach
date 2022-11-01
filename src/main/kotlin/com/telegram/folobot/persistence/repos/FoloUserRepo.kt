package com.telegram.folobot.persistence.repos

import com.telegram.folobot.persistence.entity.FoloUserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FoloUserRepo : CrudRepository<FoloUserEntity, Long> {
    fun findUserByUserId(userId: Long): FoloUserEntity?
}