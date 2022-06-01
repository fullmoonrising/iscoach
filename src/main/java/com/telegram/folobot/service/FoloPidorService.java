package com.telegram.folobot.service;

import com.telegram.folobot.domain.FoloPidorEntity;
import com.telegram.folobot.domain.FoloPidorId;
import com.telegram.folobot.dto.FoloPidorDto;
import com.telegram.folobot.mappers.FoloPidorMapper;
import com.telegram.folobot.repos.FoloPidorRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.SplittableRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FoloPidorService {
    private final FoloPidorRepo foloPidorRepo;
    private final FoloPidorMapper foloPidorMapper;

    /**
     * Прочитать все
     * @return {@link List<FoloPidorDto>}
     */
    public List<FoloPidorDto> findAll() {
        return foloPidorRepo.findAll(
                Sort.by("id.chatId").ascending()
                        .and(Sort.by("score").descending())).stream()
                .map(foloPidorMapper::mapToFoloPidorDto)
                .toList();
    }

    /**
     * Чтение по ключу
     * @param chatId Id чата
     * @param userId Id пользователя
     * @return {@link FoloPidorDto}
     */
    public FoloPidorDto findById(Long chatId, Long userId) {
        return foloPidorRepo.findById(new FoloPidorId(chatId, userId))
                .map(foloPidorMapper::mapToFoloPidorDto)
                .orElse(new FoloPidorDto(chatId, userId).setNew(true));
    }

    /**
     * Проверка существования по ключу
     * @param chatId Id чата
     * @param userId Id пользователя
     * @return да/нет
     */
    public boolean existsById(Long chatId, Long userId) {
        return foloPidorRepo.existsById(new FoloPidorId(chatId, userId));
    }

    /**
     * Получение по Id чата
     * @param chatId Id чата
     * @return {@link List<FoloPidorDto>}
     */
    public List<FoloPidorDto> findByIdChatId(Long chatId) {
        return foloPidorRepo.findByIdChatId(chatId,
                        Sort.by("id.chatId").ascending()
                                .and(Sort.by("score").descending())).stream()
                .map(foloPidorMapper::mapToFoloPidorDto)
                .toList();
    }

    /**
     * Выбор случайного фолопидора
     *
     * @param chatid ID чата
     * @return {@link FoloPidorEntity}
     */
    public FoloPidorDto getRandom(Long chatid) {
        //Получаем список фолопидоров для чата
        List<FoloPidorEntity> foloPidorEntities = foloPidorRepo.findByIdChatId(chatid);
        //Выбираем случайного
        return foloPidorMapper.mapToFoloPidorDto(foloPidorEntities
                .get(new SplittableRandom().nextInt(foloPidorEntities.size())));
    }

    /**
     * Получение топ 10 фолопидоров чата
     * @param chatId Id чата
     * @return {@link List<FoloPidorDto>}
     */
    public List<FoloPidorDto> getTop(Long chatId) {
        return foloPidorRepo.findByIdChatId(chatId)
                .stream()
                .map(foloPidorMapper::mapToFoloPidorDto)
                .filter(FoloPidorDto::hasScore)
                .collect(
                        Collectors.groupingBy(Function.identity(),
                                Collectors.summingInt(FoloPidorDto::getScore)))
                .entrySet().stream()
                .limit(10)
                .map(e -> e.getKey().setScore(e.getValue()))
                .sorted(Comparator.comparing(FoloPidorDto::getScore).reversed())
                .toList();
    }

    /**
     * Сохранение
     * @param dto {@link FoloPidorDto}
     */
    public void save(FoloPidorDto dto) {
        foloPidorRepo.save(foloPidorMapper.mapToFoloPidorEntity(dto));
    }

    /**
     * Удаление
     * @param dto {@link FoloPidorDto}
     */
    public void delete(FoloPidorDto dto) {foloPidorRepo.delete(foloPidorMapper.mapToFoloPidorEntity(dto)); }
}
