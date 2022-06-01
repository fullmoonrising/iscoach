package com.telegram.folobot.controller;

import com.telegram.folobot.constants.ControllerCommandsEnum;
import com.telegram.folobot.dto.FoloUserDto;
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
@RequestMapping("/user")
public class UserController {
    private final FoloUserService foloUserService;

    @GetMapping
    public String user(Map<String, Object> model) {
        model.put("folousers", foloUserService.findAll());
        return "user";
    }

    /**
     * Post-запрос на выполнение команды с основного экрана
     *
     * @param userId ID пользователя
     * @param tag    Переопределеннои имя
     * @param action Команда
     * @param model  Map с переменными
     * @return Имя экрана
     */
    @PostMapping
    public String onAction(
            @RequestParam Long userId,
            @RequestParam(required = false) Long mainId,
            @RequestParam(required = false) String tag,
            @RequestParam String action,
            Map<String, Object> model
    ) {
        if (!Objects.isNull(userId)) {
            switch (ControllerCommandsEnum.valueOf(action.toUpperCase())) {
                case ADD:
                    if (!foloUserService.existsById(userId)) {
                        foloUserService.save(new FoloUserDto(userId, mainId, tag));
                    }
                    break;
                case UPDATE:
                    if (foloUserService.existsById(userId)) {
                        foloUserService.save(foloUserService.findById(userId).setMainId(mainId).setTag(tag));
                    }
                    break;
                case DELETE:
                    foloUserService.delete(new FoloUserDto(userId));
                    break;
            }
        }
        return user(model);
    }
}
