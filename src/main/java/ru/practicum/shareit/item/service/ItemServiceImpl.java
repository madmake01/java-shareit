package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.exception.ForbiddenOperationException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsResponseDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
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
            throw new ItemNotFoundException(
                    "Item with id = %d not found for user %d".formatted(itemId, ownerId));
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
        return itemMapper.toResponseDtoList(
                itemRepository.findItemsByNameContainingIgnoreCaseAndAvailable(text, true));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemWithBookingsResponseDto findById(Long requesterId, Long itemId) {
        Item item = itemRepository.findByIdWithComments(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id = %d not found".formatted(itemId)));

        boolean isOwner = item.getOwner() != null && item.getOwner().getId().equals(requesterId);

        Booking last = null;
        Booking next = null;

        if (isOwner) {
            LocalDateTime now = LocalDateTime.now();
            last = bookingRepository
                    .findFirstByItemIdAndStartBeforeAndEndBeforeOrderByEndDesc(itemId, now, now)
                    .orElse(null);
            next = bookingRepository
                    .findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, now)
                    .orElse(null);
        }

        return itemMapper.toResponseDto(item, last, next);
    }


    @Override
    public Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(
                        "Item with id = %d not found".formatted(itemId)));
    }

    @Override
    public CommentResponseDto addComment(Long authorId, Long itemId, CommentCreateDto dto) {
        boolean hasPastBooking = bookingRepository
                .existsByBookerIdAndItemIdAndStartBeforeAndStatusNotIn(
                        authorId,
                        itemId,
                        LocalDateTime.now(),
                        List.of(Status.REJECTED, Status.WAITING)
                );

        if (!hasPastBooking) {
            throw new ForbiddenOperationException(
                    "User %d has not started any valid booking for item %d".formatted(authorId, itemId)
            );
        }

        User author = userService.getUserOrThrow(authorId);
        Item item = getItemOrThrow(itemId);

        Comment entity = commentMapper.toEntity(dto.text(), item, author);
        Comment saved = commentRepository.save(entity);

        return commentMapper.toResponseDto(saved);
    }

}
