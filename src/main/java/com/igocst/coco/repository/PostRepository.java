package com.igocst.coco.repository;

import com.igocst.coco.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByRecruitmentStateFalseOrderByCreateDateDesc();
    List<Post> findAllByOrderByCreateDateDesc();
    List<Post> findAllByMember_IdOrderByCreateDateDesc(Long id);
    List<Post> findAllByTitleContainingOrderByCreateDateDesc(String query);
    List<Post> findAllByRecruitmentStateFalseOrderByHitsDesc();
    List<Post> findAllByOrderByHitsDesc();
    List<Post> findAllByRecruitmentStateFalse();

    @Modifying
    @Query("update Post p set p.hits = p.hits + 1 where p.id = :id")
    int updateHits(Long id);
}