package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloDate;
import com.telegram.folobot.domain.FoloDateId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FoloDateRepo extends CrudRepository<FoloDate, FoloDateId> {
    List<FoloDate> findByChatidAndType(long chatid, String type);
}
