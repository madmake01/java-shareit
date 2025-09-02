package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    boolean existsByBookerIdAndItemIdAndStartBeforeAndStatusNotIn(
            Long bookerId,
            Long itemId,
            LocalDateTime now,
            Collection<Status> excluded
    );

    Optional<Booking> findFirstByItemIdAndStartBeforeAndEndBeforeOrderByEndDesc(
            Long itemId,
            LocalDateTime startBefore,
            LocalDateTime endBefore
    );

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);
}