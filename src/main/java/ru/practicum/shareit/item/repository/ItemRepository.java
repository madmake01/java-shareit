package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(Long id);

    void deleteById(Long id);

    List<Item> findByOwnerId(Long ownerId);

    List<Item> findByNameContainingIgnoreCase(String name);
}
