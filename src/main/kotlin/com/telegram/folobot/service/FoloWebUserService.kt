package com.telegram.folobot.service

import com.telegram.folobot.model.dto.FoloWebUserDto
import com.telegram.folobot.model.dto.toEntity
import com.telegram.folobot.persistence.entity.toDto
import com.telegram.folobot.persistence.repos.FoloWebUserRepo
import org.springframework.stereotype.Service

@Service
class FoloWebUserService(private val foloWebUserRepo: FoloWebUserRepo) {
    fun findUserByUsername(username: String): FoloWebUserDto? {
        return foloWebUserRepo.findUserByUsername(username)?.toDto()
    }

    fun save(foloWebUserDto: FoloWebUserDto) {
        foloWebUserRepo.save(foloWebUserDto.toEntity())
    }
}