package com.telegram.folobot.repos

import com.telegram.folobot.constants.VarTypeEnum
import com.telegram.folobot.domain.FoloVarEntity
import com.telegram.folobot.domain.FoloVarId
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FoloVarRepo : CrudRepository<FoloVarEntity, FoloVarId> {
    fun findVarById(foloVarId: FoloVarId): FoloVarEntity?
}