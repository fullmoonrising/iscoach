package com.telegram.folobot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
/*TODO Убрать логику в сервисы
    В сервисе получение разными способами с сортровкам и группировками, сохранение
    Новое поле mainID
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
public class FoloUser {
    @Id
    @Column(name = "userId")
    private Long userId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.userId", orphanRemoval = true)
    private Set<FoloPidor> foloPidorEntities = new HashSet<>();
    private String name;
    private String tag;

    public FoloUser() {};

    public FoloUser(Long userId) {
        this(userId, "");
    }

    public FoloUser(Long userId, String tag) {
        this.userId = userId;
        this.tag = tag;
    }

    public String getName() { return tag != null && !tag.isEmpty() ? tag : name != null ? name : ""; }

    public String getRealName() { return name != null ? name : ""; }

    public String getTag() { return tag != null ? tag : ""; }
}

