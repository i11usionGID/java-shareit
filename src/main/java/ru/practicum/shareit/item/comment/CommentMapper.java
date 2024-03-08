package ru.practicum.shareit.item.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment toComment(CommentDtoRequest request, User author, Item item) {
        return Comment.builder()
                .text(request.getText())
                .author(author)
                .item(item)
                .build();
    }

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .text(comment.getText())
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .build();
    }
}
