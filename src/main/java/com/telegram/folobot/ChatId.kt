package com.telegram.folobot

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User

class ChatId {

    companion object {
        val FOLOCHAT_ID = -1001439088515L
        val POC_ID = -1001154453685L
        val ANDREWSLEGACY_ID = -1001210743498L
        val ANDREW_ID = 146072069L
        val VITALIK_ID = 800522859L
        val FOLOMKIN_ID = 362689512L
        val MOONMOON_ID = 50496196L

        fun isFolochat(chat: Chat?): Boolean {
            return chat != null && chat.id == FOLOCHAT_ID
        }

        fun isAndrew(user: User?): Boolean {
            return user != null && user.id == ANDREW_ID
        }

        fun isVitalik(user: User?): Boolean {
            return user != null && user.id == VITALIK_ID
        }

        fun isMoonMoon(user: User?): Boolean {
            return user != null && user.id == MOONMOON_ID
        }

        fun isFo(user: User?): Boolean {
            return user != null && user.id == FOLOMKIN_ID
        }
    }
}