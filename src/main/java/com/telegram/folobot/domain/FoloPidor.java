package com.telegram.folobot.domain;

import com.telegram.folobot.repos.FoloPidorRepo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Optional;

@Entity
@IdClass(FoloPidorId.class)
@Component
@Getter
@Setter
public class FoloPidor {
    @Id
    private Long chatid;
    @Id
    @Column(name = "userid")
    private Long userid;

    @ManyToOne(fetch = FetchType.LAZY) //TODO удаление User влечет за собой удаление FoloPidor
    @JoinColumn(name = "userid", insertable = false, updatable = false)
    private FoloUser foloUser;

    private Integer score;

    @Transient
    private boolean isNew;

    public FoloPidor() {
    }

    public FoloPidor(Long chatid, Long userid) {
        this(chatid, userid, 0);
    }

    public FoloPidor(Long chatid, Long userid, Integer score) {
        this.chatid = chatid;
        this.userid = userid;
        this.score = score;
    }

    public String getName() { return Optional.ofNullable(foloUser).orElse(new FoloUser()).getName(); }

    public String getTag() { return Optional.ofNullable(foloUser).orElse(new FoloUser()).getTag(); }

    public static FoloPidor createNew(Long chatid, Long userid) {
        FoloPidor foloPidor = new FoloPidor(chatid, userid);
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

