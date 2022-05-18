package com.telegram.folobot.controller;

import com.telegram.folobot.domain.FoloPidor;
import com.telegram.folobot.domain.FoloPidorView;
import com.telegram.folobot.domain.FoloUser;
import com.telegram.folobot.repos.FoloPidorRepo;
import com.telegram.folobot.repos.FoloUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class MainController {
    @Autowired
    private FoloPidorRepo foloPidorRepo;
    @Autowired
    private FoloUserRepo foloUserRepo;

    //TODO вынести стили в CSS файл

    //TODO описание
    @GetMapping
    public String main(Map<String, Object> model) {
        model.put("folopidors", prepareToShow(foloPidorRepo.findAll()));
        return "main";
    }

    //TODO описание
    @PostMapping
    public String onAction(
            @RequestParam(name = "chatid", required = true) String chatid, //TODO команды в ENUM
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "userid", required = true) String userid,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "action", required = true) String action,
            Map<String, Object> model
    ) {
        switch (action) {
            case "add":
                if (!chatid.isEmpty() && !userid.isEmpty()) {
                    //TODO оптимизировать
//                    foloPidorRepo.findByChatidAndUserid(Long.parseLong(chatid), Long.parseLong(userid))
//                            .stream()
//                            .findAny().isPresent()
//                            .forEach();


                    List<FoloPidor> folopidors = foloPidorRepo.findByChatidAndUserid(Long.parseLong(chatid), Long.parseLong(userid));
                    if (!folopidors.isEmpty()) {
                        folopidors.forEach(foloPidor -> {
                            foloPidor.setTag(tag);
                            foloPidorRepo.save(foloPidor);
                        });
                    } else {
                        foloPidorRepo.save(new FoloPidor(Long.parseLong(chatid), Long.parseLong(userid), tag));
                    }
                }
                break;
            case "update":
                foloPidorRepo.findByChatidAndUserid(Long.parseLong(chatid), Long.parseLong(userid))
                        .forEach(foloPidor -> { foloPidor.setTag(tag); foloPidorRepo.save(foloPidor); } );
                break;
            case "delete":
                foloPidorRepo.findByChatidAndUserid(Long.parseLong(chatid), Long.parseLong(userid))
                        .forEach(foloPidor -> { foloPidorRepo.delete(foloPidor); } );
                break;
            case "filter":
                model.put("folopidors", prepareToShow( filter != null && !filter.isEmpty()
                        ? foloPidorRepo.findByChatid(Long.parseLong(filter))
                        : foloPidorRepo.findAll()));
                return "main";
        }
        return main(model);
    }

    //TODO описание
    private Iterable<FoloPidorView> prepareToShow(Iterable<FoloPidor> foloPidors) {
        return StreamSupport.stream(foloPidors.spliterator(), false)
                .sorted(Comparator.comparingLong(FoloPidor::getChatid).thenComparingInt(FoloPidor::getScore).reversed())
                .map(FoloPidorView::new)
                .map(foloPidor -> {
                    foloPidor.setName(foloUserRepo.findById(foloPidor.getUserid()).orElse(new FoloUser()).getName());
                    return foloPidor;
                })
                .collect(Collectors.toList());
    }

}
