package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.request.dto.ItemRequestIdDto;

public record ItemUpdateDto(
        String name,
        String description,
        Boolean available,
        ItemRequestIdDto request
) {}
