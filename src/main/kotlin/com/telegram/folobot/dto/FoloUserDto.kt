package com.telegram.folobot.dto

import java.io.Serializable

class FoloUserDto(
    val userId: Long,
    var mainId: Long = 0L,
    var anchor: Boolean = false,
    var name: String = "",
    var tag: String = ""
) : Serializable {

    fun getMainUserId() : Long {
        return if (mainId != 0L) mainId else userId
    }

    fun getTagName(): String { return if (tag.isNotEmpty()) tag else name }

    fun setMainId(mainId: Long): FoloUserDto {
        this.mainId = mainId
        return this
    }

    fun setAnchor(anchor: Boolean): FoloUserDto {
        this.anchor = anchor
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