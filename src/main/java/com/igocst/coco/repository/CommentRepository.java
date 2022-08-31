package com.igocst.coco.repository;

import com.igocst.coco.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);
    List<Comment> findAllByMember_IdOrderByLastModifiedDateDesc(Long id);
}
