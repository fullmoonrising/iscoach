package com.telegram.folobot.domain

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
class FoloVarId(private val chatId: Long,
                private val type: String) : Serializable {
}