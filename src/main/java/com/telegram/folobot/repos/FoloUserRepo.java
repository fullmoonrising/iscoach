package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloUser;
import com.telegram.folobot.domain.FoloVar;
import org.springframework.data.repository.CrudRepository;


public interface FoloUserRepo extends CrudRepository<FoloUser, Long> {
}
