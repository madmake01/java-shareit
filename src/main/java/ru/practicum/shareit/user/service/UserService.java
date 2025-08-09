package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {
    UserResponseDto create(UserCreateRequestDto userCreateRequestDto);

    UserResponseDto update(Long id, UserUpdateRequestDto userUpdateRequestDto);

    void delete(Long id);

    UserResponseDto findById(Long id);

    User getUserOrThrow(Long id);
}
