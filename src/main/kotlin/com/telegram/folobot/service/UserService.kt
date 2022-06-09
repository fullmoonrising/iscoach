package com.telegram.folobot.service

import com.telegram.folobot.FoloBot
import com.telegram.folobot.Utils.printExeptionMessage
import com.telegram.folobot.dto.FoloPidorDto
import com.telegram.folobot.dto.FoloUserDto
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

@Component
class UserService(private val foloUserService: FoloUserService) { //TODO kotlinise

    lateinit var foloBot: FoloBot

    /**
     * Получение имени пользователя
     *
     * @param user [User]
     * @return Имя пользователя
     */
    fun getUserName(user: User?): String? {
        return if (!Objects.isNull(user)) {
            Stream.of(Stream.of(user!!.firstName, user.lastName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" ")),
                user.userName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null)
        } else null
    }

    /**
     * Получение имени пользователя
     *
     * @param user [User]
     * @return Имя пользователя
     */
    fun getFoloUserName(user: User): String {
        val foloUser: FoloUserDto = foloUserService.findById(user.id)
        // По тэгу
        var userName: String? = foloUser.tag
        if (userName!!.isEmpty()) userName = getUserName(user)
        // По сохраненному имени
        if (userName == null || userName.isEmpty()) userName = foloUser.tag
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец"
        return userName
    }

    /**
     * Получение имени фолопидора
     *
     * @param foloPidorDto [FoloPidorDto]
     * @return Имя фолопидора
     */
    fun getFoloUserName(foloPidorDto: FoloPidorDto): String {
        // По тэгу
        var userName: String? = foloPidorDto.getTag()
        // По пользователю
        if (userName!!.isEmpty()) userName = getUserName(getUserById(foloPidorDto.id.userId))
        // По сохраненному имени
        if (userName == null || userName.isEmpty()) userName = foloPidorDto.getName()
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец"
        return userName
    }

    private fun getUserById(userid: Long): User? {
        return try {
            foloBot.execute(GetChatMember(userid.toString(), userid)).user
        } catch (ignored: TelegramApiException) {
            null
        }
    }

    /**
     * Получение кликабельного имени пользователя
     *
     * @param user [User]
     * @return Имя пользователя
     */
    private fun getFoloUserNameLinked(user: User): String {
        return "[" + getFoloUserName(user) + "](tg://user?id=" + user.id + ")"
    }

    /**
     * Получение кликабельного имени фолопидора
     *
     * @param foloPidor [FoloPidorDto]
     * @return Имя фолопидора
     */
    fun getFoloUserNameLinked(foloPidor: FoloPidorDto): String {
        return "[" + getFoloUserName(foloPidor) + "](tg://user?id=" + foloPidor.id.userId + ")"
    }

    /**
     * Проверка что [User] это этот бот
     *
     * @param user [User]
     * @return да/нет
     */
    fun isSelf(user: User): Boolean {
        return try {
            user.id == foloBot.me.id
        } catch (e: TelegramApiException) {
            printExeptionMessage(e)
            false
        }
    }
}