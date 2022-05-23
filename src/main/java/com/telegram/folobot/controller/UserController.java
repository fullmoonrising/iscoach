package com.telegram.folobot.controller;

import com.telegram.folobot.domain.FoloPidor;
import com.telegram.folobot.domain.FoloUser;
import com.telegram.folobot.repos.FoloPidorRepo;
import com.telegram.folobot.repos.FoloUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private FoloPidorRepo foloPidorRepo;
    @Autowired
    private FoloUserRepo foloUserRepo;

    @GetMapping("/user")
    public String user(Map<String, Object> model) { //Добавить расширенную инфу на просмотре
        Iterable<FoloUser> foloUsers = foloUserRepo.findAll();
        model.put("folousers", foloUsers);
        return "user";
    }

    @PostMapping("/user")
    public String updateUsers(Map<String, Object> model) {
        Iterable<FoloPidor> foloPidors = foloPidorRepo.findAll();
        foloPidorRepo.findAll().forEach(f -> {
                    FoloUser foloUser = foloUserRepo.findById(f.getUserid()).orElse(new FoloUser());
                    if (foloUser.isEmpty()) {
                        foloUserRepo.save(new FoloUser(f.getUserid(), f.getTag()));
                    }
                }
        );
        return user(model);
    }
}
