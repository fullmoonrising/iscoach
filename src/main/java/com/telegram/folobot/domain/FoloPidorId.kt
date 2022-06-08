package com.telegram.folobot.domain

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
class FoloPidorId(var chatId: Long,
                  var userId: Long) : Serializable {
}