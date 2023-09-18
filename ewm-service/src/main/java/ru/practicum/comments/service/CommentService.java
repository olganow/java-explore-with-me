package ru.practicum.comments.service;


import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createCommentPrivate(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateCommentByIdPrivate(Long userId, Long commentId, CommentDto dto);

    CommentDto getCommentByIdPrivate(Long userId, Long commentId);

    List<CommentDto> getCommentsByAuthorIdPrivate(Long userId, Integer from, Integer size);

    List<CommentDto> getCommentsByEventIdPrivate(Long userId, Long eventId, Integer from, Integer size);

    void deleteCommentByIdPrivate(Long userId, Long commentId);

    CommentDto getCommentByIdAdmin(Long commentId);

    CommentDto updateCommentAdmin(Long commentId, CommentDto commentDto);

    void deleteCommentByIdAdmin(Long commentId);

    List<CommentDto> getCommentsPublic(String text, Integer from, Integer size);

}