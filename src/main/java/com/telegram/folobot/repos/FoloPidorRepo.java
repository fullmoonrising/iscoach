package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloPidor;
import com.telegram.folobot.domain.FoloPidorId;
import org.springframework.data.repository.CrudRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface FoloPidorRepo extends CrudRepository<FoloPidor, FoloPidorId> {
    FoloPidor findByChatidAndUserid(Long chatid, Long userid);

    boolean existsByChatidAndUserid(Long chatid, Long userid);

    List<FoloPidor> findByChatid(Long chatid);

    default Iterable<FoloPidor> findAllSorted() {
        return sort(findAll());
    }

    default Iterable<FoloPidor> findByChatidSorted(Long chatid) {
        return sort(findByChatid(chatid));
    }

    default Iterable<FoloPidor> sort(Iterable<FoloPidor> foloPidors) {
        return StreamSupport.stream(foloPidors.spliterator(), false)
                .sorted(Comparator
                        .comparingLong(FoloPidor::getChatid)
                        .thenComparingInt(FoloPidor::getScore)
                        .reversed())
                .collect(Collectors.toList());
    }
}
