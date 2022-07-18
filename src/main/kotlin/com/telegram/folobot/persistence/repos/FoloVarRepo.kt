package com.telegram.folobot.persistence.repos

import com.telegram.folobot.persistence.entity.FoloVarEntity
import com.telegram.folobot.persistence.entity.FoloVarId
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FoloVarRepo : CrudRepository<FoloVarEntity, FoloVarId> {
    fun findVarById(foloVarId: FoloVarId): FoloVarEntity?
}