package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloPidor;
import com.telegram.folobot.domain.FoloPidorId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoloPidorRepo extends CrudRepository<FoloPidor, FoloPidorId> {
    List<FoloPidor> findByIdChatId(Long chatId);

//    List<FoloPidor> findAllByOrderByIdChatIdAndScoreDesc(); //TODO

    List<FoloPidor> findTop10ByIdChatIdOrderByScoreDesc(Long chatId);
}
