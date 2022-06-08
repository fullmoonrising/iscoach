package com.telegram.folobot.dto

import java.io.Serializable

class FoloUserDto(
    val userId: Long,
    private var mainId: Long? = null,
    private var name: String = "",
    private var tag: String = ""
) : Serializable {

    fun getMainUserId() : Long {
        return mainId ?: userId
    }

    fun getMainId(): Long {
        return mainId ?: 0L
    }

    fun getName(): String {
        return tag ?: name ?: ""
    }

    fun getRealName(): String {
        return name ?: ""
    }

    fun getTag(): String {
        return tag ?: ""
    }

    fun setMainId(mainId: Long): FoloUserDto {
        this.mainId = mainId
        return this
    }

    fun setName(name: String?): FoloUserDto {
        if (name != null) {
            this.name = name
        }
        return this
    }

    fun setTag(tag: String): FoloUserDto {
        this.tag = tag
        return this
    }
}