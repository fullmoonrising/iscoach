package com.telegram.folobot.service;

import com.telegram.folobot.FoloBot;
import com.telegram.folobot.dto.FoloPidorDto;
import com.telegram.folobot.dto.FoloUserDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.telegram.folobot.Utils.printExeptionMessage;

@Component
@RequiredArgsConstructor
@Accessors(chain = true)
public class UserService {
    private final FoloUserService foloUserService;

    @Setter
    private FoloBot foloBot;

    /**
     * Получение имени пользователя
     *
     * @param user {@link User}
     * @return Имя пользователя
     */
    public String getUserName(User user) {
        if (!Objects.isNull(user)) {
            return Stream.of(Stream.of(user.getFirstName(), user.getLastName())
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.joining(" ")),
                            user.getUserName())
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Получение имени пользователя
     *
     * @param user {@link User}
     * @return Имя пользователя
     */
    public String getFoloUserName(User user) {
        FoloUserDto foloUser = foloUserService.findById(user.getId());
        // По тэгу
        String userName = foloUser.getTag();
        if (userName.isEmpty()) userName = getUserName(user);
        // По сохраненному имени
        if (userName == null || userName.isEmpty()) userName = foloUser.getName();
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец";
        return userName;
    }

    /**
     * Получение имени фолопидора
     *
     * @param foloPidorDto {@link FoloPidorDto}
     * @return Имя фолопидора
     */
    public String getFoloUserName(FoloPidorDto foloPidorDto) {
        // По тэгу
        String userName = foloPidorDto.getTag();
        // По пользователю
        if (userName.isEmpty()) userName = getUserName(getUserById(foloPidorDto.getId().getUserId()));
        // По сохраненному имени
        if (userName == null || userName.isEmpty()) userName = foloPidorDto.getName();
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец";
        return userName;
    }

    private User getUserById(Long userid) {
        try {
            return foloBot.execute(new GetChatMember(Long.toString(userid), userid)).getUser();
        } catch (TelegramApiException ignored) {
            return null;
        }
    }

    /**
     * Получение кликабельного имени пользователя
     *
     * @param user {@link User}
     * @return Имя пользователя
     */
    private String getFoloUserNameLinked(User user) {
        return "[" + getFoloUserName(user) + "](tg://user?id=" + user.getId() + ")";
    }

    /**
     * Получение кликабельного имени фолопидора
     *
     * @param foloPidor {@link FoloPidorDto}
     * @return Имя фолопидора
     */
    public String getFoloUserNameLinked(FoloPidorDto foloPidor) {
        return "[" + getFoloUserName(foloPidor) + "](tg://user?id=" + foloPidor.getId().getUserId() + ")";
    }

    /**
     * Проверка что {@link User} это этот бот
     *
     * @param user {@link User}
     * @return да/нет
     */
    public boolean isSelf(User user) {
        try {
            return user != null && user.getId().equals(foloBot.getMe().getId());
        } catch (TelegramApiException e) {
            printExeptionMessage(e);
            return false;
        }
    }
}
