package ru.practicum.comments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.comments.model.Comment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Long countCommentByEventId(Long eventId);

    List<Comment> findAllByEventId(Long eventId, PageRequest pageable);

    Page<Comment> findAllByAuthorId(Long userId, PageRequest pageable);

    List<Comment> findByEventIdIn(Set<Long> longs);

    Optional<Comment> findByIdAndAuthorId(Long commentId, Long userId);

    @Query("select c " +
            "from Comment c " +
            "JOIN FETCH c.event " +
            "JOIN FETCH c.author " +
            "where (c.event.id = :event) " +
            "and (UPPER(c.text) LIKE UPPER(concat('%', :text, '%')))"
    )
    List<Comment> findAllEventIdAndByText(@Param("event") Long eventId, @Param("text") String text, PageRequest pageable);

}