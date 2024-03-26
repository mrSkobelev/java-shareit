package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class CommentMapper {
    public Comment toComment(CommentDto commentDto, User author, Item item) {
        Comment comment = new Comment();

        comment.setText(commentDto.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        return comment;
    }

    public CommentInfoDto toCommentInfoDto(Comment comment) {
        CommentInfoDto commentInfoDto = new CommentInfoDto();

        commentInfoDto.setId(comment.getId());
        commentInfoDto.setText(comment.getText());
        commentInfoDto.setCreated(comment.getCreated());
        commentInfoDto.setAuthorName(comment.getAuthor().getName());

        return commentInfoDto;
    }
}
