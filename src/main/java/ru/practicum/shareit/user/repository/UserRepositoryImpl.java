package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> storage = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public User save(User user) {
        if (emailExists(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + user.getEmail());
        }
        long id = getId();
        user.setId(id);
        storage.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        if (emailExistsForOtherUser(user.getEmail(), user.getId())) {
            throw new EmailAlreadyExistsException("Email already exists: " + user.getEmail());
        }
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    private long getId() {
        return idCounter++;
    }

    private boolean emailExists(String email) {
        return storage.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    private boolean emailExistsForOtherUser(String email, Long id) {
        return storage.values().stream()
                .anyMatch(u -> !u.getId().equals(id) && u.getEmail().equalsIgnoreCase(email));
    }
}
