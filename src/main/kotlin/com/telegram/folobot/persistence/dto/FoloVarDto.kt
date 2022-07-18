package com.telegram.folobot.persistence.dto

import com.telegram.folobot.persistence.entity.FoloVarId
import java.io.Serializable

class FoloVarDto(
    val id: FoloVarId,
    val value: String
) : Serializable {
}