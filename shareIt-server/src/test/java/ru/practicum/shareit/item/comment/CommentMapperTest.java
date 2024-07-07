package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toComment() {
        User user = new User();
        Item item = new Item();
        CommentDtoRequest commentDtoInput = CommentDtoRequest.builder()
                .text("text")
                .build();
        Comment comment = CommentMapper.toComment(commentDtoInput, user, item);
        assertEquals(commentDtoInput.getText(), comment.getText(), "некорректная работа.");
        assertEquals(user, comment.getAuthor(), "некорректная работа.");
        assertEquals(item, comment.getItem(), "некорректная работа.");
    }

    @Test
    void toDto() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(new Item())
                .author(new User())
                .created(LocalDateTime.now())
                .build();
        CommentDto commentDto = CommentMapper.toDto(comment);
        assertEquals(comment.getId(), commentDto.getId(), "");
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName(), "некорректная работа.");
        assertEquals(comment.getText(), commentDto.getText(), "некорректная работа.");
    }
}