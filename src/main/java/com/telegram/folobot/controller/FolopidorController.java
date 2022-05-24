package com.telegram.folobot.controller;

import com.telegram.folobot.domain.*;
import com.telegram.folobot.enums.*;
import com.telegram.folobot.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class FolopidorController {
    @Autowired
    private FoloPidorRepo foloPidorRepo;
    @Autowired
    private FoloUserRepo foloUserRepo;

    /**
     * Заполнение основного экрана
     * @param model Map с переменными
     * @return Имя экрана
     */
    @GetMapping("/folopidor")
    public String main(Map<String, Object> model) {
        model.put("folopidors", prepareToShow(foloPidorRepo.findAll()));
        return "folopidor";
    }

    /**
     * Post-запрос на выполнение команды с основного экрана
     * @param chatid ID чата
     * @param userid ID пользователя
     * @param tag Переопределеннои имя
     * @param action Команда
     * @param model Map с переменными
     * @return Имя экрана
     */
    @PostMapping("/folopidor")
    public String onAction(
            @RequestParam(name = "chatid", required = true) String chatid,
            @RequestParam(name = "userid", required = false) String userid,
            @RequestParam(name = "tag", required = false) String tag,
            @RequestParam(name = "action", required = true) String action,
            Map<String, Object> model
    ) {
        switch (ControllerCommands.valueOf(action)) {
            case add:
                if (!chatid.isEmpty() && !userid.isEmpty()) {
                    if (foloPidorRepo.findByChatidAndUserid(Long.parseLong(chatid), Long.parseLong(userid)).isEmpty()) {
                        foloPidorRepo.save(new FoloPidor(Long.parseLong(chatid), Long.parseLong(userid), tag));
                    }
                }
                break;
            case update:
                FoloPidor foloPidor = foloPidorRepo
                        .findByChatidAndUserid(Long.parseLong(chatid), Long.parseLong(userid));
                foloPidor.setTag(tag);
                foloPidorRepo.save(foloPidor);
                break;
            case delete:
                foloPidorRepo.delete(foloPidorRepo.
                        findByChatidAndUserid(Long.parseLong(chatid), Long.parseLong(userid)));
                break;
            case filter:
                model.put("folopidors", prepareToShow(chatid != null && !chatid.isEmpty()
                        ? foloPidorRepo.findByChatid(Long.parseLong(chatid))
                        : foloPidorRepo.findAll()));
                return "folopidor";
        }
        return main(model);
    }

    /**
     * Расширение структуры фолопидора на поле имя из таблицы пользователей
     * @param foloPidors {@link FoloPidor}
     * @return Список фолопидоров с заполненным дополнительным полем
     */
    private Iterable<FoloPidorView> prepareToShow(Iterable<FoloPidor> foloPidors) {
        return StreamSupport.stream(foloPidors.spliterator(), false)
                .sorted(Comparator.comparingLong(FoloPidor::getChatid).thenComparingInt(FoloPidor::getScore).reversed())
                .map(FoloPidorView::new)
                .peek(foloPidor -> foloPidor.setName(foloUserRepo.findById(foloPidor.getUserid()).orElse(new FoloUser()).getName()))
                .collect(Collectors.toList());
    }
}
