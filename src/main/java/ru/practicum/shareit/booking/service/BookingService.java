package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(Long requesterId, BookingCreateDto dto);

    BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved);

    BookingResponseDto getById(Long requesterId, Long bookingId);

    List<BookingResponseDto> findAllByBooker(Long bookerId,
                                             BookingState state);

    List<BookingResponseDto> findAllByOwner(Long ownerId,
                                            BookingState state);

    Booking getBookingOrThrow(Long bookingId);
}
