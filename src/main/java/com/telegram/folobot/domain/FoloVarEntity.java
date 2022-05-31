package com.telegram.folobot.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "folo_var")
public class FoloVarEntity {
    @EmbeddedId
    @NonNull
    private FoloVarId id;
    @NonNull
    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FoloVarEntity foloVarEntity = (FoloVarEntity) o;
        return id != null && Objects.equals(id, foloVarEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

