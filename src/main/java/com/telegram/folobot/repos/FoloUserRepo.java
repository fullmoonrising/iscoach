package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoloUserRepo extends CrudRepository<FoloUser, Long> {
}
