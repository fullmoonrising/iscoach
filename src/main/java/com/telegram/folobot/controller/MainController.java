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

    @GetMapping
    public String main(Map<String, Object> model) {
        List<FoloPidorView> folopidors = StreamSupport.stream(foloPidorRepo.findAll().spliterator(), false)
                .sorted(Comparator.comparingLong(FoloPidor::getChatid).thenComparingInt(FoloPidor::getScore).reversed())
                .map(FoloPidorView::new)
                .map(f -> { f.setName(foloUserRepo.findById(f.getUserid()).orElse(new FoloUser()).getName()); return f; } )
                .collect(Collectors.toList());
        model.put("folopidors", folopidors);
        return "main";
    }

    @PostMapping("main")
    public String addFolopidor(
            @RequestParam(name = "chatid", required = true ) String chatid,
            @RequestParam(name = "userid", required = true ) String userid,
            @RequestParam(name = "tag", required = false ) String tag,
            Map<String, Object> model
    ) {
        if (!chatid.isEmpty() && !userid.isEmpty()) {
            List<FoloPidor> folopidors = foloPidorRepo.findByChatidAndUserid(Long.parseLong(chatid), Long.parseLong(userid));
            if (!folopidors.isEmpty()) {
                folopidors.forEach(f -> {
                    f.setTag(tag);
                    foloPidorRepo.save(f);
                });
            } else {
                foloPidorRepo.save(new FoloPidor(Long.parseLong(chatid), Long.parseLong(userid), tag));
            }
        }
        return main(model);
    }

    @PostMapping("delete")
    public String delete( //TODO сделать нормальный шаблон, в идеале добавить кнопку удаления на таблицу
            @RequestParam(name = "chatid", required = true ) String chatid,
            @RequestParam(name = "userid", required = true ) String userid,
            Map<String, Object> model
    ){
        if (!chatid.isEmpty() && !userid.isEmpty()) {
            List<FoloPidor> folopidors = foloPidorRepo.findByChatidAndUserid(Long.parseLong(chatid), Long.parseLong(userid));
            folopidors.forEach(f -> foloPidorRepo.delete(f) );
        }
        return main(model);
    }

    @PostMapping("filter")
    public String filter( //TODO починить фильтр (сделать общий метод для обогащения данных)
            @RequestParam String filter,
            Map<String, Object> model
    ) {
        Iterable<FoloPidor> foloPidors;
        if (filter != null && !filter.isEmpty()) {
            foloPidors = foloPidorRepo.findByChatid(Long.parseLong(filter));
        } else {
            foloPidors = foloPidorRepo.findAll();
        }
        model.put("folopidors", foloPidors);
        return "main";
    }

}
