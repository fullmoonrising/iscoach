package com.telegram.folobot.domain

import java.time.LocalDate
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

    @Column(nullable = false)
    var score: Int,

    @Column(nullable = false)
    var lastWinDate: LocalDate
) {
}