package com.telegram.folobot.domain;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

//TODO Убрать логику в сервисы и DTO
//TODO тут только пустой конструктор и полный конструктор, кастомные все в DTO
//TODO переименовать таблицы в БД

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "folo_var")
public class FoloVarEntity {
    @EmbeddedId
    private FoloVarId id;
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

