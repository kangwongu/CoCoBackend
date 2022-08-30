package com.igocst.coco.domain;

import com.igocst.coco.domain.timestamped.Timestamped;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private String profileImageUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String introduction;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberRole role;

    // 게시글 양방향, 회원이 삭제되면, 게시글도 같이 삭제
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Post> posts = new ArrayList<>();

    // 댓글 양방향, 회원이 삭제되면, 댓글도 같이 삭제
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    // 쪽지 양방향, 회원이 삭제되면, 쪽지(발송한 쪽지)도 같이 삭제
    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE)
    private List<Message> sendMessage = new ArrayList<>();

    // 회원이 삭제되면, 쪽지(수신한 쪽지)도 같이 삭제
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE)
    private List<Message> readMessage = new ArrayList<>();

    // 북마크
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Builder
    public Member(Long id, String email, String password, String nickname, String profileImageUrl, String githubUrl,
                  String portfolioUrl, String introduction, MemberRole role, List<Post> posts,
                  List<Comment> comments, List<Message> sendMessage, List<Message> readMessage, List<Bookmark> bookmarks) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.githubUrl = githubUrl;
        this.portfolioUrl = portfolioUrl;
        this.introduction = introduction;
        this.role = role;
        this.posts = posts;
        this.comments = comments;
        this.sendMessage = sendMessage;
        this.readMessage = readMessage;
        this.bookmarks = bookmarks;
    }

    /**
     * 연관관계 메소드
     */
    // 회원이 작성한 게시글 추가
    public void addPost(Post post) {
        post.registerMember(this);
        posts.add(post);
    }

    // 회원의 게시글 중에서 특정 게시글 삭제
    public boolean deletePost(Long postId) {
        if (postId <= 0) {
            return false;
        }
        for (Post post : posts) {
            if (post.getId() == postId){
                posts.remove(post);
                return true;
            }
        }
        return false;
    }

    // 회원이 작성한 특정 게시글을 찾는다.
    public Optional<Post> findPost(Long postId) {
        if (postId <= 0) {
            return Optional.empty();
        }
        for (Post post : posts) {
            if (post.getId() == postId) {
                return Optional.ofNullable(post);
            }
        }
        return Optional.empty();
    }

    // 연관관계 메소드 / 댓글 - 회원
    public void createComment(Comment comment) {
        comment.registerMember(this);
        comments.add(comment);
    }

    //회원이 작성한 댓글 찾기
    public Optional<Comment> findComment(Long commentId) {
        if (commentId <= 0) {
            return Optional.empty();
        }
        for (Comment comment : comments) {
            Long com = comment.getId();
            if (com.equals(commentId)) {
                return Optional.ofNullable(comment);
            }
        }
        return Optional.empty();
    }

    public void sendMessage(Message message) {
        this.sendMessage.add(message);
        if (message.getSender() != this) {
            message.sendMember(this);
        }
    }

    // 회원이 받은 쪽지를 찾는다.
    public Optional<Message> findMessage(Long messageId) {
        if (messageId <= 0) {
            return Optional.empty();
        }
        for (Message message : readMessage) {
            if (message.getId() == messageId) {
                return Optional.ofNullable(message);
            }
        }
        for (Message message : sendMessage) {
            if (message.getId() == messageId) {
                return Optional.ofNullable(message);
            }
        }
        return Optional.empty();
    }

    // 회원이 받은 쪽지를 삭제한다.
    public boolean deleteMessage(Long messageId) {
        if (messageId <= 0) {
            return false;
        }
        // 리스트를 돌아서 해당하는 쪽지 찾는다
        Iterator<Message> iterator = readMessage.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(messageId)) {
                iterator.remove();
                return true;
            }
        }
        Iterator<Message> sendIterator = sendMessage.iterator();
        while (sendIterator.hasNext()) {
            if (sendIterator.next().getId().equals(messageId)) {
                sendIterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean deleteComment(Long commentId) {
        if (commentId <= 0) {
            return false;
        }
        // 리스트를 돌아서 해당하는 게시글을 찾는다
        Iterator<Comment> iterator = comments.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(commentId)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    // 회원정보 수정
    public void updateNickname(String nickname){ this.nickname = nickname; }

    public void updateGithubUrl(String githubUrl){
        this.githubUrl = githubUrl;
    }

    public void updatePortfolioUrl(String portfolioUrl){
        this.portfolioUrl = portfolioUrl;
    }

    public void updateIntroduction(String introduction){
        this.introduction = introduction;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // 북마크
    public void addBookmark(Bookmark bookmark) {
        bookmark.registerMember(this);
        bookmarks.add(bookmark);
    }

    public boolean deleteBookmark(Long postId) {
        if (postId <= 0) {
            return false;
        }

        for (Bookmark bookmark : bookmarks) {
            if (bookmark.getId() == postId){
                bookmarks.remove(bookmark);
                return true;
            }
        }
        return false;
    }
}