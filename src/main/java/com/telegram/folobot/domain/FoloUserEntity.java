package com.telegram.folobot.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "folo_user")
public class FoloUserEntity {
    @Id
    @Column(name = "userId")
    @NonNull
    private Long userId;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "foloUserEntity", orphanRemoval = true)
    private Set<FoloPidorEntity> foloPidorEntities = new HashSet<>();
    private Long mainId;
    private String name;
    private String tag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FoloUserEntity that = (FoloUserEntity) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

