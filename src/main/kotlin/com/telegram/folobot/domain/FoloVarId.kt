package com.telegram.folobot.domain

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
class FoloVarId(private val chatId: Long,
                private val type: String) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoloVarId

        if (chatId != other.chatId) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chatId.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}