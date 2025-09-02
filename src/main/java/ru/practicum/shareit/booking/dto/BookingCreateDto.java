package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.practicum.shareit.booking.validation.EndAfterStart;

import java.time.LocalDateTime;

@EndAfterStart
public record BookingCreateDto(
        @NotNull
        @Positive
        Long itemId,
        @NotNull
        @FutureOrPresent
        LocalDateTime start,
        @FutureOrPresent
        @NotNull
        LocalDateTime end
) {
}
