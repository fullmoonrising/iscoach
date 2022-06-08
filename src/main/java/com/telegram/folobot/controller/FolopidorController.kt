package com.telegram.folobot.controller

import com.telegram.folobot.constants.ControllerCommandsEnum
import com.telegram.folobot.dto.FoloPidorDto
import com.telegram.folobot.service.FoloPidorService
import com.telegram.folobot.service.FoloUserService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

//TODO логику из контроллеров вынести в сервисы
//TODO проверки ввода
@Controller
@RequestMapping("/folopidor")
class FolopidorController(
    private val foloPidorService: FoloPidorService,
    private val foloUserService: FoloUserService
) {

    /**
     * Заполнение основного экрана
     * @param model Map с переменными
     * @return Имя экрана
     */
    @GetMapping
    fun main(model: MutableMap<String, Any>): String {
        model["folopidors"] = foloPidorService.findAll()
        return "folopidor"
    }

    /**
     * Post-запрос на выполнение команды с основного экрана
     * @param chatId ID чата
     * @param userId ID пользователя
     * @param score Счет
     * @param action Команда
     * @param model Map с переменными
     * @return Имя экрана
     */
    @PostMapping
    fun onAction(
        @RequestParam chatId: Long,
        @RequestParam(required = false) userId: Long,
        @RequestParam(defaultValue = "0", required = false) score: Int,
        @RequestParam action: String,
        model: MutableMap<String, Any>
    ): String {
        when (ControllerCommandsEnum.valueOf(action.uppercase(Locale.getDefault()))) {
            ControllerCommandsEnum.ADD -> if (foloUserService.existsById(userId) &&
                !foloPidorService.existsById(chatId, userId)
            ) {
                foloPidorService.save(FoloPidorDto(chatId, userId, score))
            }
            ControllerCommandsEnum.UPDATE -> if (foloPidorService.existsById(chatId, userId)) {
                val foloPidor = foloPidorService.findById(chatId, userId) //TODO прокачать
                foloPidor.score = score
                foloPidorService.save(foloPidor)
            }
            ControllerCommandsEnum.DELETE -> foloPidorService.delete(FoloPidorDto(chatId, userId))
            ControllerCommandsEnum.FILTER -> {
                model["folopidors"] =
                    if (!Objects.isNull(chatId)) foloPidorService.findByIdChatId(chatId) else foloPidorService.findAll()
                return "folopidor"
            }
        }
        return main(model)
    }
}