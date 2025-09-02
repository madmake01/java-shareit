package ru.practicum.shareit.booking.contoller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto create(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @Valid @RequestBody BookingCreateDto dto
    ) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(
            @RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
            @PathVariable @Positive Long bookingId,
            @RequestParam("approved") boolean approved
    ) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(
            @RequestHeader("X-Sharer-User-Id") @Positive Long requesterId,
            @PathVariable @Positive Long bookingId
    ) {
        return bookingService.getById(requesterId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> findAllByBooker(
            @RequestHeader("X-Sharer-User-Id") @Positive Long bookerId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam
    ) {
        BookingState state = parseStateOrThrow(stateParam);
        return bookingService.findAllByBooker(bookerId, state).stream()
                .sorted(Comparator.comparing(BookingResponseDto::start).reversed())
                .toList();
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findAllByOwner(
            @RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam
    ) {
        BookingState state = parseStateOrThrow(stateParam);
        return bookingService.findAllByOwner(ownerId, state).stream()
                .sorted(Comparator.comparing(BookingResponseDto::start).reversed())
                .toList();
    }

    private static BookingState parseStateOrThrow(String raw) {
        try {
            return BookingState.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown state: " + raw);
        }
    }
}
