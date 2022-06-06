package com.telegram.folobot.controller;

import com.telegram.folobot.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//TODO логику из контроллеров вынести в сервисы
//TODO проверки ввода

@Controller
@AllArgsConstructor
@RequestMapping("/main")
public class MainController {
    private final MessageService messageService;

    @GetMapping
    public String main() {
        return "main";
    }
    @PostMapping
    public String sendMessage(@RequestParam Long chatId, @RequestParam String message ) {
        messageService.sendMessage(message, chatId);
        return "main";
    }

}
