package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloPidor;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface FolopidorRepo extends CrudRepository<FoloPidor, Integer> {
    List<FoloPidor> findByChatidAndUserid(long chatid, long userid);

    List<FoloPidor> findByChatid(long chatid);
}
