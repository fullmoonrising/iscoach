package com.telegram.folobot.controller;

import com.telegram.folobot.domain.FoloPidor;
import com.telegram.folobot.domain.FoloUser;
import com.telegram.folobot.enums.ControllerCommands;
import com.telegram.folobot.repos.FoloPidorRepo;
import com.telegram.folobot.repos.FoloUserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@AllArgsConstructor
public class FolopidorController {
    private final FoloPidorRepo foloPidorRepo;
    private final FoloUserRepo foloUserRepo;

    /**
     * Заполнение основного экрана
     * @param model Map с переменными
     * @return Имя экрана
     */
    @GetMapping("/folopidor")
    public String main(Map<String, Object> model) {
        model.put("folopidors", StreamSupport.stream(foloPidorRepo.findAll().spliterator(), false)
                .sorted(Comparator
                        .comparingLong(FoloPidor::getChatid)
                        .thenComparingInt(FoloPidor::getScore)
                        .reversed())
                .collect(Collectors.toList()));
        return "folopidor";
    }

    /**
     * Post-запрос на выполнение команды с основного экрана
     * @param chatid ID чата
     * @param userid ID пользователя
     * @param score Счет
     * @param action Команда
     * @param model Map с переменными
     * @return Имя экрана
     */
    @PostMapping("/folopidor")
    public String onAction(
            @RequestParam(name = "chatid", required = true) String chatid,
            @RequestParam(name = "userid", required = false) String userid,
            @RequestParam(name = "score", required = false) String score,
            @RequestParam(name = "action", required = true) String action,
            Map<String, Object> model
    ) {
        switch (ControllerCommands.valueOf(action)) {
            case add: //TODO после добавления не подтягивается имя
                if (!chatid.isEmpty() && !userid.isEmpty() &&
                        !foloPidorRepo.existsByChatidAndUserid(Long.parseLong(chatid), Long.parseLong(userid)) &&
                        foloUserRepo.existsById(Long.parseLong(userid))) {
                    foloPidorRepo.save(new FoloPidor(Long.parseLong(chatid),
                            Long.parseLong(userid),
                            Integer.parseInt(score)));
                }
                break;
            case update:
                FoloPidor foloPidor = foloPidorRepo
                        .findByChatidAndUserid(Long.parseLong(chatid), Long.parseLong(userid));
                foloPidor.setScore(Integer.parseInt(score));
                foloPidorRepo.save(foloPidor);
                break;
            case delete:
                foloPidorRepo.delete(foloPidorRepo
                        .findByChatidAndUserid(Long.parseLong(chatid), Long.parseLong(userid)));
                break;
            case filter:
                model.put("folopidors", chatid != null && !chatid.isEmpty()
                        ? foloPidorRepo.findByChatid(Long.parseLong(chatid))
                        : foloPidorRepo.findAll());
                return "folopidor";
        }
        return main(model);
    }
}
