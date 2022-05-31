package com.telegram.folobot.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "folo_pidor")
public class FoloPidorEntity {
    @EmbeddedId
    @NonNull
    private FoloPidorId id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private FoloUserEntity foloUserEntity;

    @NonNull
    private Integer score;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FoloPidorEntity that = (FoloPidorEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

