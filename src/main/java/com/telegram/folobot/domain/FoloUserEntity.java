package com.telegram.folobot.domain;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/*TODO Убрать логику в сервисы и DTO
    В сервисе получение разными способами с сортровкам и группировками, сохранение
    Новое поле mainID
 */
//TODO тут только пустой конструктор и полный конструктор, кастомные все в DTO
//TODO переименовать таблицы в БД

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "folo_user")
public class FoloUserEntity {
    @Id
    @Column(name = "userId")
    private Long userId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userId", orphanRemoval = true)
    @ToString.Exclude //TODO проверить мэппинг
    private Set<FoloPidorEntity> foloPidorEntityEntities = new HashSet<>();
    private String name;
    private String tag;

    public FoloUserEntity(Long userId) {
        this(userId, "");
    }

    public FoloUserEntity(Long userId, String tag) {
        this.userId = userId;
        this.tag = tag;
    }

    public String getName() { return tag != null && !tag.isEmpty() ? tag : name != null ? name : ""; }

    public String getRealName() { return name != null ? name : ""; }

    public String getTag() { return tag != null ? tag : ""; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FoloUserEntity that = (FoloUserEntity) o;
        return userId != null && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

