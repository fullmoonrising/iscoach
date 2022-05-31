package com.telegram.folobot.controller;

import com.telegram.folobot.Bot;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//TODO логику из контроллеров вынести в сервисы
//TODO проверки ввода
//TODO CamelCase

@Controller
@AllArgsConstructor
@RequestMapping("/main")
public class MainController {
    private final Bot bot;

    @GetMapping
    public String main() {
        return "main";
    }
    @PostMapping
    public String sendMessage(@RequestParam String chatid, @RequestParam String message ) {
        bot.sendMessage(message, Long.parseLong(chatid));
        return "main";
    }

}
