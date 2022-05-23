package com.telegram.folobot.controller;

import com.telegram.folobot.Bot;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;


@RestController
@AllArgsConstructor
public class WebhookController {
    private final Bot bot;

    @GetMapping
    public ResponseEntity get() { return ResponseEntity.ok().build(); }

    @PostMapping
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return bot.onWebhookUpdateReceived(update);
    }

}
