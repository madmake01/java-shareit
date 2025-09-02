package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestIdDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public record ItemWithBookingsResponseDto(
        Long id,
        String name,
        String description,
        boolean available,
        UserResponseDto userResponseDto,
        ItemRequestIdDto request,
        BookingResponseDto lastBooking,
        BookingResponseDto nextBooking,
        List<CommentResponseDto> comments
) {
}
