package com.telegram.folobot;

import com.telegram.folobot.domain.FoloPidor;
import com.telegram.folobot.repos.FolopidorRepo;
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
    private FolopidorRepo folopidorRepo;

    @GetMapping
    public String main(Map<String, Object> model) {
        List<FoloPidor> folopidors = StreamSupport.stream(folopidorRepo.findAll().spliterator(), false)
                .sorted(Comparator.comparingLong(FoloPidor::getChatid).thenComparingInt(FoloPidor::getScore).reversed())
                .collect(Collectors.toList());
        model.put("folopidors", folopidors);
        return "main";
    }

    @PostMapping("main")
    public String addFolopidor(
            @RequestParam(name = "chatid", required = true, defaultValue = "-1001439088515" )  long chatid, //TODO
            @RequestParam(name = "userid", required = true ) long userid,
//            @RequestParam(name = "score", required = false, value = "0" ) int score,
            @RequestParam(name = "tag", required = false ) String tag,
            Map<String, Object> model
    ) {
        List<FoloPidor> folopidors = folopidorRepo.findByChatidAndUserid(chatid, userid);
        if (!folopidors.isEmpty()) {
            folopidors.forEach(f -> {
                f.setTag(tag);
                folopidorRepo.save(f);
            });
        } else {
            folopidorRepo.save(new FoloPidor(chatid, userid, tag));
        }
        return main(model);
    }

    @PostMapping("filter")
    public String filter(
            @RequestParam String filter,
            Map<String, Object> model
    ) {
        Iterable<FoloPidor> folopidors;
        if (filter != null && !filter.isEmpty()) {
            folopidors = folopidorRepo.findByChatid(Long.parseLong(filter));
        } else {
            folopidors = folopidorRepo.findAll();
        }
        model.put("folopidors", folopidors);
        return "main";
    }

}
