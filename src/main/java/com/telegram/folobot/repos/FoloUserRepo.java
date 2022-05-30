package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloUserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoloUserRepo extends CrudRepository<FoloUserEntity, Long> {
}
