package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
            SELECT i
            FROM Item i
            LEFT JOIN FETCH i.comments
            WHERE i.id = :id
            """)
    Optional<Item> findByIdWithComments(@Param("id") Long id);

    List<Item> findByOwnerId(Long ownerId);

    List<Item> findItemsByNameContainingIgnoreCaseAndAvailable(String name, boolean available);
}
