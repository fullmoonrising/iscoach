package com.telegram.folobot.model.dto

import com.telegram.folobot.persistence.entity.FoloWebUserEntity
import com.telegram.folobot.persistence.entity.Role

data class FoloWebUserDto(
    var username: String,
    var password: String,
    var active: Boolean,
    var roles: Set<Role>
)

fun FoloWebUserDto.toEntity(): FoloWebUserEntity = FoloWebUserEntity(
    username = username,
    password = password,
    active = active,
    roles = roles
)