package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", source = "item")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "text", source = "text")
    Comment toEntity(String text, Item item, User author);

    @Mapping(target = "authorName", source = "author.name")
    CommentResponseDto toResponseDto(Comment comment);

    List<CommentResponseDto> map(List<Comment> comments);
}
