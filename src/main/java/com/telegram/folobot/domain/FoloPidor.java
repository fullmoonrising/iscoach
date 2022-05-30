package com.telegram.folobot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Optional;
//TODO Убрать логику в сервисы
//TODO metauser
@Entity
@Getter
@Setter
@AllArgsConstructor
public class FoloPidor {
    @EmbeddedId
    private FoloPidorId id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private FoloUser foloUser;

    private Integer score;

    @Transient
    private boolean isNew;

    public FoloPidor() {};

    public FoloPidor(FoloPidorId id) {
        this(id, 0);
    }

    public FoloPidor(FoloPidorId id, Integer score) {
        this.id = id;
        this.score = score;
    }

    public String getName() { return Optional.ofNullable(foloUser).orElse(new FoloUser()).getName(); }

    public String getTag() { return Optional.ofNullable(foloUser).orElse(new FoloUser()).getTag(); }

    public static FoloPidor createNew(FoloPidorId id) {
        return createNew(id, 0);
    }
    public static FoloPidor createNew(FoloPidorId id, Integer score) {
        FoloPidor foloPidor = new FoloPidor(id, score);
        foloPidor.foloUser = new FoloUser(id.getUserId());
        foloPidor.setNew(true);
        return foloPidor;
    }

    public boolean isNew() {
        return isNew;
    }

    public boolean hasScore() {
        return this.score > 0;
    }
}

