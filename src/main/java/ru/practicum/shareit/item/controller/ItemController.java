package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.HeaderConstants;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemWithBookingsResponseDto findById(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @PathVariable Long itemId
    ) {
        return itemService.findById(requesterId, itemId);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto create(
            @RequestHeader(HeaderConstants.USER_ID_HEADER) Long ownerId,
            @Validated @RequestBody ItemCreateDto dto
    ) {
        return itemService.create(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(
            @PathVariable Long itemId,
            @RequestHeader(HeaderConstants.USER_ID_HEADER) Long ownerId,
            @Validated @RequestBody ItemUpdateDto dto
    ) {
        return itemService.update(itemId, ownerId, dto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long itemId) {
        itemService.delete(itemId);
    }

    @GetMapping
    public List<ItemResponseDto> findByOwner(
            @RequestHeader(HeaderConstants.USER_ID_HEADER) Long ownerId
    ) {
        return itemService.findByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchByName(
            @RequestParam String text
    ) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        return itemService.searchByName(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto addComment(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @PathVariable @Positive Long itemId,
            @Valid @RequestBody CommentCreateDto dto
    ) {
        return itemService.addComment(userId, itemId, dto);
    }
}
