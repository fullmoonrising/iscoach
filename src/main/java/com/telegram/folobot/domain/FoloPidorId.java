package com.telegram.folobot.domain;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Setter
@Getter
@EqualsAndHashCode
public class FoloPidorId implements Serializable {
    private long chatId;
    private long userId;

    public FoloPidorId() {};

    public FoloPidorId(Long chatId, Long userId) {
        this.chatId = chatId;
        this.userId = userId;
    }
}
