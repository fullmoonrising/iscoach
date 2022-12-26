package com.telegram.folobot.controller

import com.telegram.folobot.model.dto.FoloWebUserDto
import com.telegram.folobot.persistence.entity.Role
import com.telegram.folobot.service.FoloWebUserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/registration")
class RegistrationController(
    private val foloWebUserService: FoloWebUserService,
    private val passwordEncoder: PasswordEncoder
) {
    @GetMapping
    fun registration(): String {
        return "registration"
    }

    @PostMapping
    fun addUser(username: String, password: String, model: MutableMap<String, Any>): String {
        foloWebUserService.findUserByUsername(username)?.run {
            model["message"] = "User already exists!"
            return "registration"
        }

        foloWebUserService.save(FoloWebUserDto(username, passwordEncoder.encode(password), true, setOf(Role.USER)))

        return "redirect:/login"
    }
}