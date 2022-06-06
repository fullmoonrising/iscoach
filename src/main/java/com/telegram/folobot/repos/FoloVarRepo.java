package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloVarEntity;
import com.telegram.folobot.domain.FoloVarId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoloVarRepo extends CrudRepository<FoloVarEntity, FoloVarId> {
}
