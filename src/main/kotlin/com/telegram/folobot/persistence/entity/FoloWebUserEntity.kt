package com.telegram.folobot.persistence.entity

import com.telegram.folobot.model.dto.FoloWebUserDto
import javax.persistence.*

@Entity
@Table(name = "folo_web_user")
class FoloWebUserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long?,
    var username: String,
    var password: String,
    var active: Boolean,
    @ElementCollection(targetClass = Role::class, fetch = FetchType.EAGER)
    @CollectionTable(name = "folo_web_user_role", joinColumns = [JoinColumn(name = "id")])
    @Enumerated(EnumType.STRING)
    var roles: Set<Role>
)

fun FoloWebUserEntity.toDto(): FoloWebUserDto = FoloWebUserDto(
    username = username,
    password = password,
    active = active,
    roles = roles
)