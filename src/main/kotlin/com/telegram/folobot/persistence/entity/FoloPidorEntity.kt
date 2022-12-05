package com.telegram.folobot.persistence.entity

import com.telegram.folobot.model.dto.FoloPidorDto
import java.time.LocalDate
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

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
    var lastWinDate: LocalDate,

    @Column(nullable = false)
    var lastActiveDate: LocalDate,

    @Column(nullable = false)
    var messagesPerDay: Int
)

fun FoloPidorEntity.toDto(): FoloPidorDto = FoloPidorDto(
    id = this.id,
    foloUser = this.foloUserEntity.toDto(),
    score = this.score,
    lastWinDate = this.lastWinDate,
    lastActiveDate = this.lastActiveDate,
    messagesPerDay = this.messagesPerDay
)