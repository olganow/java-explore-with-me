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


    List<Comment> findAllByEventId(Long eventId);

    List<Comment> findAllByEventId(Long eventId, PageRequest pageable);

    Page<Comment> findAllByAuthorId(Long userId, PageRequest pageable);

    List<Comment> findByEventIdIn(Set<Long> longs);

    Optional<Comment> findByIdAndAuthorId(Long commentId, Long userId);


    @Query("select c " +
            "from Comment AS c " +
            "JOIN FETCH c.event " +
            "JOIN FETCH c.author " +
            "where (:author is null or c.author.id in :author) " +
            "and (:event is null or c.event.id in :event)"
    )
    List<Comment> findAllByAuthorIdOrEventId(Long author, Long event, PageRequest pageable);

    @Query("select c " +
            "from Comment c " +
            "JOIN FETCH c.event " +
            "JOIN FETCH c.author " +
            "where (UPPER(c.text) LIKE UPPER(concat('%', :text, '%')))"

    )
    List<Comment> findAllByText(@Param("text") String text, PageRequest pageable);

}