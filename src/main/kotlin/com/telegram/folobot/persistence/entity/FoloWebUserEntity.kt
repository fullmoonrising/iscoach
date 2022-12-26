package com.telegram.folobot.persistence.entity

import com.telegram.folobot.model.dto.FoloWebUserDto
import jakarta.persistence.*

@Entity
@Table(name = "folo_web_user")
class FoloWebUserEntity(
    @Id
    var username: String,
    var password: String,
    var active: Boolean,
    @ElementCollection(targetClass = Role::class, fetch = FetchType.EAGER)
    @CollectionTable(name = "folo_web_user_role", joinColumns = [JoinColumn(name = "username")])
    @Enumerated(EnumType.STRING)
    var roles: Set<Role>
)

fun FoloWebUserEntity.toDto(): FoloWebUserDto = FoloWebUserDto(
    username = username,
    password = password,
    active = active,
    roles = roles
)