package com.telegram.folobot.controller;

import com.telegram.folobot.constants.ControllerCommandsEnum;
import com.telegram.folobot.dto.FoloPidorDto;
import com.telegram.folobot.service.FoloPidorService;
import com.telegram.folobot.service.FoloUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Objects;

//TODO логику из контроллеров вынести в сервисы
//TODO проверки ввода

@Controller
@AllArgsConstructor
@RequestMapping("/folopidor")
public class FolopidorController {
    private final FoloPidorService foloPidorService;
    private final FoloUserService foloUserService;

    /**
     * Заполнение основного экрана
     * @param model Map с переменными
     * @return Имя экрана
     */
    @GetMapping
    public String main(Map<String, Object> model) {
        model.put("folopidors", foloPidorService.findAll());
        return "folopidor";
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
    public String onAction(
            @RequestParam Long chatId,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0", required = false ) Integer score,
            @RequestParam String action,
            Map<String, Object> model
    ) {
        switch (ControllerCommandsEnum.valueOf(action.toUpperCase())) {
            case ADD:
                if (foloUserService.existsById(userId) &&
                        !foloPidorService.existsById(chatId, userId)) {
                    foloPidorService.save(new FoloPidorDto(chatId, userId, score));
                }
                break;
            case UPDATE:
                if (foloPidorService.existsById(chatId, userId)) {
                    foloPidorService.save(foloPidorService.findById(chatId, userId)
                            .setScore(score));
                }
                break;
            case DELETE:
                foloPidorService.delete(new FoloPidorDto(chatId, userId));
                break;
            case FILTER:
                model.put("folopidors", !Objects.isNull(chatId)
                        ? foloPidorService.findByIdChatId(chatId)
                        : foloPidorService.findAll());
                return "folopidor";
        }
        return main(model);
    }
}
