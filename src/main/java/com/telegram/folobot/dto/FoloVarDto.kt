package com.telegram.folobot.dto

import com.telegram.folobot.domain.FoloVarId
import java.io.Serializable

class FoloVarDto(
    val id: FoloVarId,
    val value: String
) : Serializable {
}