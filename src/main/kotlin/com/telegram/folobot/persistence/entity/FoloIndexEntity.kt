package com.telegram.folobot.persistence.entity

import com.telegram.folobot.model.dto.FoloIndexDto
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "folo_index")
class FoloIndexEntity(
    @EmbeddedId
    @Column(nullable = false)
    val id: FoloIndexId,
    val points: Int,
    val index: Double?
)

fun FoloIndexEntity.toDto(): FoloIndexDto = FoloIndexDto(
    id = this.id,
    points = this.points,
    index = this.index
)