package com.telegram.folobot.controller;

import com.telegram.folobot.domain.FoloUser;
import com.telegram.folobot.enums.ControllerCommands;
import com.telegram.folobot.repos.FoloUserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@AllArgsConstructor
public class UserController {
    private final FoloUserRepo foloUserRepo;

    @GetMapping("/user")
    public String user(Map<String, Object> model) {
        model.put("folousers", foloUserRepo.findAll());
        return "user";
    }

     /** Post-запрос на выполнение команды с основного экрана
     * @param userid ID пользователя
     * @param tag Переопределеннои имя
     * @param action Команда
     * @param model Map с переменными
     * @return Имя экрана
     */
    @PostMapping("/user")
    public String onAction(
            @RequestParam String userid,
            @RequestParam(required = false) String tag,
            @RequestParam String action,
            Map<String, Object> model
    ) {
        switch (ControllerCommands.valueOf(action)) {
            case add:
                if (!userid.isEmpty() && !foloUserRepo.existsById(Long.parseLong(userid))) {
                    foloUserRepo.save(new FoloUser(Long.parseLong(userid), tag));
                }
                break;
            case update:
                FoloUser foloUser = foloUserRepo.findById(Long.parseLong(userid)).orElseThrow();
                foloUser.setTag(tag);
                foloUserRepo.save(foloUser);
                break;
            case delete:
                foloUserRepo.delete(foloUserRepo.findById(Long.parseLong(userid)).orElseThrow());
                break;
        }
        return user(model);
    }
}
