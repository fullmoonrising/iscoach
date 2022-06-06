package com.telegram.folobot.repos;

import com.telegram.folobot.domain.FoloUserEntity;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoloUserRepo extends CrudRepository<FoloUserEntity, Long> {
    @NonNull List<FoloUserEntity> findAll();
}
