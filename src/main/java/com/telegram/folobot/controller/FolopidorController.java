package com.telegram.folobot.controller;

import com.telegram.folobot.domain.FoloPidor;
import com.telegram.folobot.domain.FoloPidorId;
import com.telegram.folobot.constants.ControllerCommandsEnum;
import com.telegram.folobot.repos.FoloPidorRepo;
import com.telegram.folobot.repos.FoloUserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Optional;

import static com.telegram.folobot.domain.FoloPidor.createNew;
//TODO логику из контроллеров вынести в сервисы
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
        model.put("folopidors", foloPidorRepo.findAll()); //TODO
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
            @RequestParam String chatid,
            @RequestParam(required = false) String userid,
            @RequestParam(required = false) String score,
            @RequestParam String action,
            Map<String, Object> model
    ) {
        Optional<FoloPidor> foloPidor = foloPidorRepo
                .findById(new FoloPidorId(Long.parseLong(chatid), Long.parseLong(userid)));
        switch (ControllerCommandsEnum.valueOf(action)) {
            case add:
                if (foloPidor.isEmpty() && foloUserRepo.existsById(Long.parseLong(userid))) {
                    foloPidorRepo.save(createNew(new FoloPidorId(Long.parseLong(chatid), Long.parseLong(userid)),
                            Integer.parseInt(score)));
                }
                break;
            case update:
                if (foloPidor.isPresent()) {
                    foloPidor.get().setScore(Integer.parseInt(score));
                    foloPidorRepo.save(foloPidor.get());
                }
                break;
            case delete:
                foloPidor.ifPresent(foloPidorRepo::delete);
                break;
            case filter:
                model.put("folopidors", !chatid.isEmpty()
                        ? foloPidorRepo.findByIdChatId(Long.parseLong(chatid)) //TODO
                        : foloPidorRepo.findAll());
                return "folopidor";
        }
        return main(model);
    }
}
