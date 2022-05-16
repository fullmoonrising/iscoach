package com.telegram.folobot;

import com.telegram.folobot.domain.Folopidor;
import com.telegram.folobot.repos.FolopidorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class MainController {
    @Autowired
    private FolopidorRepo folopidorRepo;

    @GetMapping
    public String main(Map<String, Object> model) {
        Iterable<Folopidor> messages = folopidorRepo.findAll();
        model.put("messages", messages);
        return "main";
    }

    @PostMapping("main")
    public String addMessage(
            @RequestParam String text,
            @RequestParam String tag,
            Map<String, Object> model
    ) {
        Folopidor message = new Folopidor(text, tag);
        folopidorRepo.save(message);
        return main(model);
    }

    @PostMapping("filter")
    public String filter(
            @RequestParam String filter,
            Map<String, Object> model
    ) {
        Iterable<Folopidor> messages;
        if (filter != null && !filter.isEmpty()) {
            messages = folopidorRepo.findByTag(filter);
        } else {
            messages = folopidorRepo.findAll();
        }
        model.put("messages", messages);
        return "main";
    }


    @GetMapping("/bot")
    public String bot( Map<String, Object> model ) {
        return "bot";
    }

}
