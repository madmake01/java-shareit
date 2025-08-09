package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    User update(User user);

    void deleteById(Long id);

    Optional<User> findById(Long id);
}
