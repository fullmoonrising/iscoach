package com.telegram.folobot.controller;

import com.telegram.folobot.Bot;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@AllArgsConstructor
public class MainController {
    private final Bot bot;

    @GetMapping("/main")
    public String main() {
        return "main";
    }
    @PostMapping("/main")
    public String sendMessage(@RequestParam String chatid, @RequestParam String message ) {
        bot.sendMessage(message, Long.parseLong(chatid));
        return "main";
    }

}
