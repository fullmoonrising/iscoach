package com.telegram.folobot.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
public class FoloUserDto implements Serializable {
    private Long userId;
    private Long mainId;
    private String name;
    private String tag;

    public FoloUserDto(Long userId) {
        this(userId, "");
    }

    public FoloUserDto(Long userId, String tag) { this(userId, null, tag); }

    public FoloUserDto(Long userId, Long mainId, String tag) {
        this.userId = userId;
        this.mainId = mainId;
        this.tag = tag;
    }

    public Long getMainUserId() {
        return Stream.of(mainId, userId)
                .filter(f -> !Objects.isNull(f) && f != 0L)
                .findFirst()
                .orElse(null);
    }

    public Long getMainId() { return !Objects.isNull(mainId) ? mainId : 0L; }

    public String getName() { return !Objects.isNull(tag) && !tag.isEmpty() ? tag : name != null ? name : ""; }

    public String getRealName() { return !Objects.isNull(name) ? name : ""; }

    public String getTag() { return !Objects.isNull(tag) ? tag : ""; }
}
