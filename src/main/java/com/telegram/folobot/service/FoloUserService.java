package com.telegram.folobot.service;

import com.telegram.folobot.domain.FoloUserEntity;
import com.telegram.folobot.dto.FoloUserDto;
import com.telegram.folobot.mappers.FoloUserMapper;
import com.telegram.folobot.repos.FoloUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FoloUserService {
    private final FoloUserRepo foloUserRepo;
    private final FoloUserMapper foloUserMapper;

    /**
     * Прочитать все
     * @return {@link List<FoloUserDto>}
     */
    public List<FoloUserDto> findAll() {
        return foloUserRepo.findAll().stream()
                .map(foloUserMapper::mapToFoloUserDto)
                .toList();
    }

    /**
     * Чтение по Id
     * @param userId {@link Long} Id
     * @return {@link FoloUserDto}
     */
    public FoloUserDto findById(Long userId) {
        return foloUserMapper.mapToFoloUserDto(foloUserRepo.findById(userId)
                        .orElse(new FoloUserEntity(userId)));
    }

    /**
     * Проверка наличия
     * @param userId Id пользователя
     * @return да/нет
     */
    public boolean existsById(Long userId) {
        return foloUserRepo.existsById(userId);
    }

    /**
     * Сохранение
     * @param dto {@link FoloUserDto}
     */
    public void save(FoloUserDto dto) {
        foloUserRepo.save(foloUserMapper.mapToFoloUserEntity(dto));
    }

    /**
     * Удаление
     * @param dto {@link FoloUserDto}
     */
    public void delete(FoloUserDto dto) {foloUserRepo.delete(foloUserMapper.mapToFoloUserEntity(dto)); }
}
