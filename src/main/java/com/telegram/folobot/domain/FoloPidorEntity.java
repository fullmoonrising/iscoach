package com.telegram.folobot.domain;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Optional;

//TODO Убрать логику в сервисы и DTO
//TODO тут только пустой конструктор и полный конструктор, кастомные все в DTO
//TODO переименовать таблицы в БД

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "folo_pidor")
public class FoloPidorEntity {
    @EmbeddedId
    private FoloPidorId id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    @ToString.Exclude
    private FoloUserEntity foloUserEntity;

    private Integer score;

    @Transient
    private boolean isNew;

    public FoloPidorEntity(FoloPidorId id) {
        this(id, 0);
    }

    public FoloPidorEntity(FoloPidorId id, Integer score) {
        this.id = id;
        this.score = score;
    }

    public String getName() { return Optional.ofNullable(foloUserEntity).orElse(new FoloUserEntity()).getName(); }

    public String getTag() { return Optional.ofNullable(foloUserEntity).orElse(new FoloUserEntity()).getTag(); }

    public static FoloPidorEntity createNew(FoloPidorId id) {
        return createNew(id, 0);
    }
    public static FoloPidorEntity createNew(FoloPidorId id, Integer score) {
        FoloPidorEntity foloPidorEntity = new FoloPidorEntity(id, score);
        foloPidorEntity.foloUserEntity = new FoloUserEntity(id.getUserId());
        foloPidorEntity.setNew(true);
        return foloPidorEntity;
    }

    public boolean isNew() {
        return isNew;
    }

    public boolean hasScore() {
        return this.score > 0;
    }

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

