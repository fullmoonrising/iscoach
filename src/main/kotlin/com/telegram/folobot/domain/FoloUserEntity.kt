package com.telegram.folobot.domain

import javax.persistence.*

@Entity
@Table(name = "folo_user")
class FoloUserEntity(
    @Id
    @Column(name = "userId", nullable = false)
    val userId: Long,

    @Column(nullable = false)
    var mainId: Long = 0L,
    @Column(nullable = false)
    var name: String = "",
    @Column(nullable = false)
    var tag: String = ""
) {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "foloUserEntity", orphanRemoval = true)
    val foloPidorEntities: MutableSet<FoloPidorEntity> = mutableSetOf()
}