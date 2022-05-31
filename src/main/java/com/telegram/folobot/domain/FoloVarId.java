package com.telegram.folobot.domain;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class FoloVarId implements Serializable {
    @NonNull private Long chatId;
    @NonNull private String type;
}
