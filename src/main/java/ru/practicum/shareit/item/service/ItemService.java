package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemResponseDto create(Long ownerId, ItemCreateDto dto);

    ItemResponseDto update(Long itemId, Long ownerId, ItemUpdateDto dto);

    void delete(Long itemId);

    List<ItemResponseDto> findByOwner(Long ownerId);

    List<ItemResponseDto> searchByName(String text);

    ItemWithBookingsResponseDto findById(Long requesterId, Long itemId);

    Item getItemOrThrow(Long itemId);

    CommentResponseDto addComment(Long authorId, Long itemId, CommentCreateDto dto);
}
