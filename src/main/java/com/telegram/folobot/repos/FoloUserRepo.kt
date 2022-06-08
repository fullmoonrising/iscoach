package com.telegram.folobot.repos

import com.telegram.folobot.domain.FoloUserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FoloUserRepo : CrudRepository<FoloUserEntity, Long> {
}