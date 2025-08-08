package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.request.dto.ItemRequestIdDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

public record ItemResponseDto(
        Long id,
        String name,
        String description,
        boolean available,
        UserResponseDto userResponseDto,
        ItemRequestIdDto request
) {
}
