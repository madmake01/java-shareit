package ru.practicum.shareit.booking.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.BookingNotAvailableException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.ForbiddenOperationException;
import ru.practicum.shareit.booking.exception.InvalidBookingStateException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    @Override
    public BookingResponseDto create(Long requesterId, BookingCreateDto dto) {
        User user = userService.getUserOrThrow(requesterId);
        Item item = itemService.getItemOrThrow(dto.itemId());

        if (!item.isAvailable()) {
            throw new BookingNotAvailableException("Item is not available for booking");
        }

        Booking saved = bookingRepository.save(bookingMapper.toEntity(dto, item, user));
        return bookingMapper.toDto(saved);
    }

    @Transactional
    @Override
    public BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenOperationException(
                    "Only item owner can approve/reject this booking: bookingId=" + bookingId
            );
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new InvalidBookingStateException(
                    "Booking status must be WAITING to change approval state"
            );
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved);
    }

    @Override
    public BookingResponseDto getById(Long requesterId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);

        boolean isBooker = booking.getBooker().getId().equals(requesterId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(requesterId);
        if (!isBooker && !isOwner) {
            throw new ForbiddenOperationException(
                    "Access denied to booking: bookingId=" + bookingId + ", requesterId=" + requesterId
            );
        }
        return bookingMapper.toDto(booking);
    }

    @Transactional
    @Override
    public List<BookingResponseDto> findAllByBooker(Long bookerId, BookingState state) {
        userService.getUserOrThrow(bookerId);
        List<Booking> result = bookingRepository.findAll(byState(state, bookerId, Instant.now()), SORT_BY_START_DESC);
        return bookingMapper.toDto(result);
    }

    @Transactional
    @Override
    public List<BookingResponseDto> findAllByOwner(Long ownerId, BookingState state) {
        userService.getUserOrThrow(ownerId);
        List<Booking> result = bookingRepository.findAll(byOwnerState(ownerId, state, Instant.now()), SORT_BY_START_DESC);
        return bookingMapper.toDto(result);
    }

    @Override
    public Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id=" + bookingId + " not found"));
    }

    private static Specification<Booking> byState(BookingState state, Long userId, Instant now) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("booker").get("id"), userId));
            predicates.add(statePredicate(state, root, cb, now));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<Booking> byOwnerState(Long ownerId, BookingState state, Instant now) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("item").get("owner").get("id"), ownerId));
            predicates.add(statePredicate(state, root, cb, now));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate statePredicate(BookingState state,
                                            Root<Booking> root,
                                            CriteriaBuilder cb,
                                            Instant now) {
        return switch (state) {
            case CURRENT -> cb.and(
                    cb.lessThanOrEqualTo(root.get("start"), now),
                    cb.greaterThanOrEqualTo(root.get("end"), now)
            );
            case PAST -> cb.lessThan(root.get("end"), now);
            case FUTURE -> cb.greaterThan(root.get("start"), now);
            case WAITING -> cb.equal(root.get("status"), Status.WAITING);
            case REJECTED -> cb.equal(root.get("status"), Status.REJECTED);
            case ALL -> cb.conjunction();
        };
    }
}
