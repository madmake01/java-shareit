package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto create(UserCreateRequestDto request) {
        return userMapper.toResponseDto(
                userRepository.save(userMapper.toEntity(request))
        );
    }

    @Override
    public UserResponseDto update(Long id, UserUpdateRequestDto request) {
        User user = getUserOrThrow(id);
        userMapper.updateEntity(request, user);
        userRepository.update(user);
        return userMapper.toResponseDto(user);
    }

    @Override
    public void delete(Long id) {
        getUserOrThrow(id);
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDto findById(Long id) {
        return userMapper.toResponseDto(getUserOrThrow(id));
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id = %d not found".formatted(id)));
    }
}
