package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booker", source = "user")
    @Mapping(target = "status", constant = "WAITING")
    Booking toEntity(BookingCreateDto bookingCreateDto, Item item, User user);

    BookingResponseDto toDto(Booking booking);

    List<BookingResponseDto> toDto(List<Booking> bookings);
}