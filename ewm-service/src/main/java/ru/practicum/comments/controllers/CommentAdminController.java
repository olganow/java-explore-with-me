package ru.practicum.comments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentAdminController {

    private final CommentService commentService;


    @GetMapping("/{commentId}")
    public CommentDto getByIdAdmin(@PathVariable(value = "commentId") Long commentId) {
        log.info("Get comment with id {}", commentId);
        return commentService.getCommentByIdAdmin(commentId);
    }


    @PatchMapping("/{commentId}")
    public CommentDto updateAdmin(@PathVariable(value = "commentId") Long commentId,
                                  @Valid @RequestBody CommentDto commentDto) {
        log.info("Update comment {} with id= {}", commentDto, commentId);
        return commentService.updateCommentAdmin(commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByIdAdmin(@PathVariable(value = "commentId") Long commentId) {
        log.info("Delete comments with id= {}", commentId);
        commentService.deleteCommentByIdAdmin(commentId);
    }

}