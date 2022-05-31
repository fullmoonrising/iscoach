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
//TODO CamelCase

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
     * @param userid ID пользователя
     * @param tag    Переопределеннои имя
     * @param action Команда
     * @param model  Map с переменными
     * @return Имя экрана
     */
    @PostMapping
    public String onAction(
            @RequestParam Long userid,
            @RequestParam(required = false) Long mainid,
            @RequestParam(required = false) String tag,
            @RequestParam String action,
            Map<String, Object> model
    ) {
        if (!Objects.isNull(userid)) {
            switch (ControllerCommandsEnum.valueOf(action.toUpperCase())) {
                case ADD:
                    if (!foloUserService.existsById(userid)) {
                        foloUserService.save(new FoloUserDto(userid, mainid, tag));
                    }
                    break;
                case UPDATE:
                    if (foloUserService.existsById(userid)) {
                        foloUserService.save(foloUserService.findById(userid).setMainId(mainid).setTag(tag));
                    }
                    break;
                case DELETE:
                    foloUserService.delete(new FoloUserDto(userid));
                    break;
            }
        }
        return user(model);
    }
}
