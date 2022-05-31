package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloPidorEntity;
import com.telegram.folobot.domain.FoloPidorId;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoloPidorRepo extends CrudRepository<FoloPidorEntity, FoloPidorId> {
    List<FoloPidorEntity> findAll(Sort sort);

    List<FoloPidorEntity> findByIdChatId(Long chatId);

    List<FoloPidorEntity> findByIdChatId(Long chatId, Sort sort);
}
