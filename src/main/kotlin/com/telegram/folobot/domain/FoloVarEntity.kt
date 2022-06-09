package com.telegram.folobot.domain

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "folo_var")
class FoloVarEntity(
    @EmbeddedId
    @Column(nullable = false)
    var id: FoloVarId,
    @Column(nullable = false)
    var value: String
) {
}