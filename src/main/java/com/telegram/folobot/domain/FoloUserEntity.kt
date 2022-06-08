package com.telegram.folobot.domain

import javax.persistence.*

@Entity
@Table(name = "folo_user")
class FoloUserEntity(
    @Id
    @Column(name = "userId", nullable = false)
    val userId: Long,

    var mainId: Long? = null,
    var name: String? = null,
    var tag: String? = null
) {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "foloUserEntity", orphanRemoval = true)
    val foloPidorEntities: MutableSet<FoloPidorEntity> = mutableSetOf()
}