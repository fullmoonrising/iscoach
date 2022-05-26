package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloVar;
import com.telegram.folobot.domain.FoloVarId;
import org.springframework.data.repository.CrudRepository;

public interface FoloVarRepo extends CrudRepository<FoloVar, FoloVarId> {
    FoloVar findByChatidAndType(Long chatid, String type);
}
