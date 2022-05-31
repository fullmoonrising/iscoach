package com.telegram.folobot.domain;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class FoloPidorId implements Serializable {
    @NonNull private Long chatId;
    @NonNull private Long userId;
}
