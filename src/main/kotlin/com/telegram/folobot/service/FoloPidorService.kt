package com.telegram.folobot.service

import com.telegram.folobot.persistence.dto.FoloPidorDto
import com.telegram.folobot.persistence.entity.FoloPidorId
import com.telegram.folobot.persistence.mappers.FoloPidorMapper
import com.telegram.folobot.persistence.repos.FoloPidorRepo
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

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
            .map { foloPidorEntity -> foloPidorMapper.mapToFoloPidorDto(foloPidorEntity) }
    }

    /**
     * Чтение по ключу
     * @param chatId Id чата
     * @param userId Id пользователя
     * @return [FoloPidorDto]
     */
    fun findById(chatId: Long, userId: Long): FoloPidorDto {
        return foloPidorRepo.findById(FoloPidorId(chatId, userId))
            .map { foloPidorEntity -> foloPidorMapper.mapToFoloPidorDto(foloPidorEntity) }
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
            .map { foloPidorEntity -> foloPidorMapper.mapToFoloPidorDto(foloPidorEntity) }
    }

    /**
     * Получение фолопидора с максимальным количеством сообщений за день по Id чата
     * @param chatId Id чата
     * @return [<]
     */
    fun findFirstByIdChatIdOrderByMessagesPerDayDesc(chatId: Long): FoloPidorDto? {
        return foloPidorRepo.findFirstByIdChatIdOrderByMessagesPerDayDesc(chatId)
            ?.let { foloPidorMapper.mapToFoloPidorDto(it) }
    }

    /**
     * Выбор случайного фолопидора
     *
     * @param chatId ID чата
     * @return [FoloPidorDto]
     */
    fun getRandom(chatId: Long): FoloPidorDto {
        return foloPidorRepo.findByIdChatId(chatId)
            .map { foloPidorEntity -> foloPidorMapper.mapToFoloPidorDto(foloPidorEntity) }
            .filter { it.isAnchored() || (it.isValid() && userService.isInChat(it, chatId)) }
            .random()
    }

    /**
     * Получение топ 10 фолопидоров чата
     * @param chatId Id чата
     * @return [<]
     */
    fun getTop(chatId: Long): List<FoloPidorDto> {
        return foloPidorRepo.findByIdChatId(chatId)
            .map { foloPidorEntity -> foloPidorMapper.mapToFoloPidorDto(foloPidorEntity) }
            .filter { foloPidorDto -> foloPidorDto.isValidTop() }
            .sortedWith(
                Comparator.comparing(FoloPidorDto::score)
                    .thenComparing(FoloPidorDto::lastWinDate).reversed()
            )
            .take(10)
    }

    /**
     * Паассивные фолопидоры чата
     * @param chatId Id чата
     * @return [<]
     */
    fun getSlackers(chatId: Long): List<FoloPidorDto> {
        return foloPidorRepo.findByIdChatId(chatId)
            .map { foloPidorEntity -> foloPidorMapper.mapToFoloPidorDto(foloPidorEntity) }
            .filter { foloPidorDto -> userService.isInChat(foloPidorDto, chatId) && foloPidorDto.isValidSlacker() }
            .sortedBy { foloPidorDto -> foloPidorDto.lastActiveDate }
            .take(10)
    }

    /**
     * Получение списка андердогов
     * @param chatId Id чата
     * @return list of [FoloPidorDto]
     */
    fun getUnderdogs(chatId: Long): List<FoloPidorDto> {
        return foloPidorRepo.findByIdChatId(chatId)
            .map { foloPidorEntity -> foloPidorMapper.mapToFoloPidorDto(foloPidorEntity) }
            .filter { foloPidorDto -> userService.isInChat(foloPidorDto, chatId) && foloPidorDto.isValidUnderdog() }
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