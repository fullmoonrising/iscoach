package com.telegram.folobot.controller;

import com.telegram.folobot.Bot;
import com.telegram.folobot.domain.FoloPidor;
import com.telegram.folobot.enums.ControllerCommands;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@Controller
@AllArgsConstructor
public class MainController {
    private final Bot bot;

    @GetMapping("/main")
    public String main(Map<String, Object> model) {
        return "main";
    }
    @PostMapping("/main")
    public String sendMessage(@RequestParam String chatid, @RequestParam String message ) {
        bot.sendMessage(message, Long.parseLong(chatid));
        return "main";
    }

}
