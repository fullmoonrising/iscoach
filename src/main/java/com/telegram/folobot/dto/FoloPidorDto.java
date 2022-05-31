package com.telegram.folobot.dto;

import com.telegram.folobot.domain.FoloPidorId;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
public class FoloPidorDto implements Serializable {
    private FoloPidorId id;
    private FoloUserDto foloUser;
    private Integer score;
    private boolean isNew;

    public FoloPidorDto(Long chatId, Long userId) { this(new FoloPidorId(chatId, userId)); }
    public FoloPidorDto(FoloPidorId id) { this(id, 0); }
    public FoloPidorDto(Long chatId, Long userId, Integer score) { this( new FoloPidorId(chatId, userId), score);}
    public FoloPidorDto(FoloPidorId id, Integer score) {
        this.id = id;
        this.foloUser = new FoloUserDto(id.getUserId());
        this.score = score;
    }

    public Long getMainUserId() { return foloUser.getMainUserId(); }

    /**
     * Получить имя пользователя
     * @return Имя
     */
    public String getName() { return Optional.ofNullable(foloUser).orElse(new FoloUserDto()).getName(); }

    /**
     * Получить тэг
     * @return Тэг
     */
    public String getTag() { return Optional.ofNullable(foloUser).orElse(new FoloUserDto()).getTag(); }

    /**
     * Проверка наличичия побед
     * @return да/нет
     */
    public boolean hasScore() { return this.score > 0; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FoloPidorDto that)) return false;
        return getMainUserId().equals(that.getMainUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMainUserId());
    }
}
