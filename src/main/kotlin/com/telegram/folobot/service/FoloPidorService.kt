package com.telegram.folobot.service

import com.telegram.folobot.domain.FoloPidorEntity
import com.telegram.folobot.domain.FoloPidorId
import com.telegram.folobot.dto.FoloPidorDto
import com.telegram.folobot.mappers.FoloPidorMapper
import com.telegram.folobot.repos.FoloPidorRepo
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.util.*

@Component
class FoloPidorService(
    private val foloPidorRepo: FoloPidorRepo,
    private val foloPidorMapper: FoloPidorMapper,
    private val userService: UserService
) {

    /**
     * Прочитать все
     * @return [<]
     */
    fun findAll(): List<FoloPidorDto> {
        return foloPidorRepo.findAll(
                Sort.by("id.chatId").ascending()
                   .and(Sort.by("score").descending())
            )
            .map(foloPidorMapper::mapToFoloPidorDto)
    }

    /**
     * Чтение по ключу
     * @param chatId Id чата
     * @param userId Id пользователя
     * @return [FoloPidorDto]
     */
    fun findById(chatId: Long, userId: Long): FoloPidorDto {
        return foloPidorRepo.findById(FoloPidorId(chatId, userId))
            .map(foloPidorMapper::mapToFoloPidorDto)
            .orElse(FoloPidorDto(chatId, userId))
    }

    /**
     * Проверка существования по ключу
     * @param chatId Id чата
     * @param userId Id пользователя
     * @return да/нет
     */
    fun existsById(chatId: Long, userId: Long): Boolean {
        return foloPidorRepo.existsById(FoloPidorId(chatId, userId))
    }

    /**
     * Получение по Id чата
     * @param chatId Id чата
     * @return [<]
     */
    fun findByIdChatId(chatId: Long): List<FoloPidorDto> {
        return foloPidorRepo.findByIdChatId(
                chatId,
                Sort.by("id.chatId").ascending()
                    .and(Sort.by("score").descending())
            )
            .map(foloPidorMapper::mapToFoloPidorDto)
    }

    /**
     * Выбор случайного фолопидора
     *
     * @param chatId ID чата
     * @return [FoloPidorEntity]
     */
    fun getRandom(chatId: Long): FoloPidorDto {
        //Получаем список фолопидоров для чата
        val foloPidors = foloPidorRepo.findByIdChatId(chatId)
            .map(foloPidorMapper::mapToFoloPidorDto)
            .filterNot(FoloPidorDto::isTwink)
            .filter { userService.isInChat(it, chatId) }
        //Выбираем случайного
        return foloPidors[SplittableRandom().nextInt(foloPidors.size)]
    }

    /**
     * Получение топ 10 фолопидоров чата
     * @param chatId Id чата
     * @return [<]
     */
    fun getTop(chatId: Long): List<FoloPidorDto> {
        return foloPidorRepo.findByIdChatId(chatId)
            .map(foloPidorMapper::mapToFoloPidorDto)
            .filter(FoloPidorDto::isValid)
            .sortedWith(Comparator.comparing(FoloPidorDto::score)
                .thenComparing(FoloPidorDto::lastWinDate).reversed())
            .take(10)
    }

    /**
     * Сохранение
     * @param dto [FoloPidorDto]
     */
    fun save(dto: FoloPidorDto) {
        foloPidorRepo.save(foloPidorMapper.mapToFoloPidorEntity(dto))
    }

    /**
     * Удаление
     * @param dto [FoloPidorDto]
     */
    fun delete(dto: FoloPidorDto) {
        foloPidorRepo.delete(foloPidorMapper.mapToFoloPidorEntity(dto))
    }
}