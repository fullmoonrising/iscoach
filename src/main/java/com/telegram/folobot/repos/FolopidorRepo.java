package com.telegram.folobot.repos;

import com.telegram.folobot.domain.Folopidor;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface FolopidorRepo extends CrudRepository<Folopidor, Integer> {
    List<Folopidor> findByTag(String tag);
}
