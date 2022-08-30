package com.igocst.coco.domain;

import com.igocst.coco.domain.timestamped.Timestamped;
import com.igocst.coco.dto.post.PostUpdateRequestDto;
import com.nhncorp.lucy.security.xss.XssPreventer;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private String period;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private boolean recruitmentState;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int hits;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Builder
    public Post(Long id, Member member, String title, String content, String period, String contact,
                boolean recruitmentState, int hits, MeetingType meetingType, List<Comment> comments,
                List<Bookmark> bookmarks) {
        this.id = id;
        this.member = member;
        this.title = title;
        this.content = content;
        this.period = period;
        this.contact = contact;
        this.recruitmentState = recruitmentState;
        this.hits = hits;
        this.meetingType = meetingType;
        this.comments = comments;
        this.bookmarks = bookmarks;
    }

    /*
     * 비즈니스 로직
     * 게시글 수정 */
    public void updatePost(PostUpdateRequestDto requestDto) {
        this.title = XssPreventer.escape(requestDto.getTitle());
        this.content = XssPreventer.escape(requestDto.getContent());
        this.recruitmentState = requestDto.isRecruitmentState();
        this.meetingType = requestDto.getMeetingType();
        this.period = requestDto.getPeriod();
        this.contact = requestDto.getContact();
    }

    //댓글 - 주인이 POST
    public void createComment(Comment comment) {
        comment.registerPost(this);
        comments.add(comment);
    }

    // 북마크
    public void addBookmark(Bookmark bookmark) {
        bookmark.registerPost(this);
        bookmarks.add(bookmark);
    }

    // 게시글 작성한 회원
    public void registerMember(Member member) { this.member = member; }
}
