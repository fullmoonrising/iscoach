package com.telegram.folobot.persistence.repos

import com.telegram.folobot.persistence.entity.FoloWebUserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FoloWebUserRepo : CrudRepository<FoloWebUserEntity, Long> {
    fun findUserByUsername(username: String): FoloWebUserEntity?
}