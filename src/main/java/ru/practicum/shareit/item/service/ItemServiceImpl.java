package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Override
    public ItemResponseDto create(Long ownerId, ItemCreateDto dto) {
        User owner = userService.getUserOrThrow(ownerId);
        Item item = itemMapper.toEntity(dto, owner, null);
        return itemMapper.toResponseDto(itemRepository.save(item));
    }

    @Override
    public ItemResponseDto update(Long itemId, Long ownerId, ItemUpdateDto dto) {
        Item existing = getItemOrThrow(itemId);

        if (!Objects.equals(existing.getOwner().getId(), ownerId)) {
            throw new ItemNotFoundException("Item with id = %d not found for user %d".formatted(itemId, ownerId));
        }

        itemMapper.updateEntity(dto, existing);
        return itemMapper.toResponseDto(itemRepository.save(existing));
    }

    @Override
    public void delete(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemResponseDto> findByOwner(Long ownerId) {
        userService.getUserOrThrow(ownerId);
        return itemMapper.toResponseDtoList(itemRepository.findByOwnerId(ownerId));
    }

    @Override
    public List<ItemResponseDto> searchByName(String text) {
        return itemMapper.toResponseDtoList(itemRepository.findItemsByNameContainingIgnoreCaseAndAvailable(text, true));
    }

    @Override
    public ItemResponseDto findById(Long itemId) {
        return itemMapper.toResponseDto(getItemOrThrow(itemId));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id = %d not found".formatted(itemId)));
    }
}
