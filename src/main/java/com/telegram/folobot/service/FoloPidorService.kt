package com.telegram.folobot.service

import com.telegram.folobot.domain.FoloPidorEntity
import com.telegram.folobot.domain.FoloPidorId
import com.telegram.folobot.dto.FoloPidorDto
import com.telegram.folobot.mappers.FoloPidorMapper
import com.telegram.folobot.repos.FoloPidorRepo
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

@Component
class FoloPidorService(
    private val foloPidorRepo: FoloPidorRepo,
    private val foloPidorMapper: FoloPidorMapper
) {

    /**
     * Прочитать все
     * @return [<]
     */
    fun findAll(): List<FoloPidorDto> {
        return foloPidorRepo.findAll(
            Sort.by("id.chatId").ascending()
                .and(Sort.by("score").descending())
        ).stream()
            .map(foloPidorMapper::mapToFoloPidorDto)
            .toList()
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
            .orElse(FoloPidorDto(chatId, userId)) //.setNew())
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
        ).stream()
            .map(foloPidorMapper::mapToFoloPidorDto)
            .toList()
    }

    /**
     * Выбор случайного фолопидора
     *
     * @param chatid ID чата
     * @return [FoloPidorEntity]
     */
    fun getRandom(chatid: Long): FoloPidorDto {
        //Получаем список фолопидоров для чата
        val foloPidorEntities = foloPidorRepo.findByIdChatId(chatid)
        //Выбираем случайного
        return foloPidorMapper.mapToFoloPidorDto(foloPidorEntities[SplittableRandom().nextInt(foloPidorEntities.size)])
    }

    /**
     * Получение топ 10 фолопидоров чата
     * @param chatId Id чата
     * @return [<]
     */
    fun getTop(chatId: Long): List<FoloPidorDto> {
        return foloPidorRepo.findByIdChatId(chatId)
            .stream()
            .map(foloPidorMapper::mapToFoloPidorDto)
            .filter(FoloPidorDto::hasScore)
            .collect(
                Collectors.groupingBy(Function.identity(),
                    Collectors.summingInt(FoloPidorDto::score)))
            .entries.stream()
            .limit(10)
            .map { entry -> entry.key.updateScore(entry.value) }
            .sorted(Comparator.comparing(FoloPidorDto::score).reversed())
            .toList()
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