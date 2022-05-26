package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloPidor;
import com.telegram.folobot.domain.FoloPidorId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FoloPidorRepo extends CrudRepository<FoloPidor, FoloPidorId> {
    FoloPidor findByChatidAndUserid(Long chatid, Long userid);

    boolean existsByChatidAndUserid(Long chatid, Long userid);

    List<FoloPidor> findByChatid(Long chatid);
}
