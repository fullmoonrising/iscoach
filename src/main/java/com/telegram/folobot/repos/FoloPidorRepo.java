package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloPidor;
import com.telegram.folobot.domain.FoloPidorId;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface FoloPidorRepo extends CrudRepository<FoloPidor, FoloPidorId> {
    List<FoloPidor> findByChatidAndUserid(long chatid, long userid);

    List<FoloPidor> findByChatid(long chatid);
}
