package com.telegram.folobot.domain

import javax.persistence.*

@Entity
@Table(name = "folo_pidor")
class FoloPidorEntity(
    @EmbeddedId
    @Column(nullable = false)
    val id: FoloPidorId,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    var foloUserEntity: FoloUserEntity,

    var score: Int
) {
}