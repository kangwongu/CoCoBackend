package com.igocst.coco.service;

import com.igocst.coco.common.status.StatusCode;
import com.igocst.coco.common.status.StatusMessage;
import com.igocst.coco.domain.Member;
import com.igocst.coco.domain.MemberRole;
import com.igocst.coco.domain.Post;
import com.igocst.coco.dto.post.*;
import com.igocst.coco.repository.MemberRepository;
import com.igocst.coco.repository.PostRepository;
import com.igocst.coco.security.MemberDetails;
import com.nhncorp.lucy.security.xss.XssPreventer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 게시글 생성
    @Transactional
    public ResponseEntity<PostSaveResponseDto> createPost(PostSaveRequestDto postSaveRequestDto, MemberDetails memberDetails) {
        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());
        Member member = memberOptional.get();

        // 게시글 길이 2000자 제한
        if (postSaveRequestDto.getContent().length() > 2000) {
            log.error("nickname={}, error={}", member.getNickname(), "게시글 길이 2000자 초과");
            return new ResponseEntity<>(
                    PostSaveResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }

        Post post = Post.builder()
                .title(XssPreventer.escape(postSaveRequestDto.getTitle()))
                .content(XssPreventer.escape(postSaveRequestDto.getContent()))
                .meetingType(postSaveRequestDto.getMeetingType())
                .contact(postSaveRequestDto.getContact())
                .period(postSaveRequestDto.getPeriod())
                .build();

        member.addPost(post);
        postRepository.save(post);

        return new ResponseEntity<>(
                PostSaveResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 게시글 내용(상세) 조회
    @Transactional
    public ResponseEntity<PostReadResponseDto> readPost(Long postId, MemberDetails memberDetails, HttpServletRequest request, HttpServletResponse response) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            log.error("nickname={}, error={}", memberDetails.getNickname(), "해당 게시글 존재하지 않음");
            return new ResponseEntity<>(
                    PostReadResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }
        Post findPost = postOptional.get();

        boolean enableUpdate = false;
        boolean enableDelete = false;

        if (memberDetails.getMember().getId() == findPost.getMember().getId()) {
            enableUpdate = true;
            enableDelete = true;
        }

        MemberRole memberRole = MemberRole.MEMBER;
        if (memberDetails.getMember().getRole().equals(MemberRole.ADMIN)) {
            memberRole = MemberRole.ADMIN;
            enableDelete = true;
        }

        // 조회 수 중복 방지
        updateHits(postId, request, response);

        return new ResponseEntity<>(
                PostReadResponseDto.builder()
                        .status(StatusMessage.SUCCESS)
                        .id(findPost.getId())
                        .title(findPost.getTitle())
                        .content(findPost.getContent())
                        .meetingType(findPost.getMeetingType())
                        .contact(findPost.getContact())
                        .period(findPost.getPeriod())
                        .recruitmentState(findPost.isRecruitmentState())
                        .hits(findPost.getHits())
                        .postDate(findPost.getCreateDate())
                        .writer(findPost.getMember().getNickname())
                        .githubUrl(findPost.getMember().getGithubUrl())
                        .portfolioUrl(findPost.getMember().getPortfolioUrl())
                        .introduction(findPost.getMember().getIntroduction())
                        .profileImageUrl(findPost.getMember().getProfileImageUrl())
                        .loginProfileImage(memberDetails.getMember().getProfileImageUrl())
                        .enableUpdate(enableUpdate)
                        .enableDelete(enableDelete)
                        .memberRole(memberRole)
                        .build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 조회 수 중복 방지
    private void updateHits(Long postId, HttpServletRequest request, HttpServletResponse response) {
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("postView")) {
                    oldCookie = cookie;
                }
            }
        }

        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("["+ postId.toString() +"]")) {
                increaseHits(postId);
                oldCookie.setValue(oldCookie.getValue() + "_[" + postId + "]");
                oldCookie.setPath("/");
                oldCookie.setDomain("cocoding.xyz");
                oldCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(oldCookie);
            }
        } else {
            increaseHits(postId);
            Cookie newCookie = new Cookie("postView", "[" + postId + "]");
            newCookie.setPath("/");
            newCookie.setDomain("cocoding.xyz");
            newCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(newCookie);
        }
    }

    // 조회수 증가 로직
    @Transactional
    public void increaseHits(Long id) {
        postRepository.updateHits(id);
    }

    public ResponseEntity<List<PostReadResponseDto>> readPostList() {
        List<Post> posts = postRepository.findAllByOrderByCreateDateDesc();
        List<PostReadResponseDto> postList = new ArrayList<>();

        for (Post post : posts) {
            postList.add(PostReadResponseDto.builder()
                    .status(StatusMessage.SUCCESS)
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .meetingType(post.getMeetingType())
                    .contact(post.getContact())
                    .period(post.getPeriod())
                    .recruitmentState(post.isRecruitmentState())
                    .hits(post.getHits())
                    .postDate(post.getCreateDate())
                    .writer(post.getMember().getNickname())
                    .build()
            );
        }
        return new ResponseEntity<>(postList, HttpStatus.valueOf(StatusCode.SUCCESS));

    }

    // 게시글 목록 조회 (모집 중인거만)
    public ResponseEntity<List<PostReadResponseDto>> readRecruitingPostList() {
        List<Post> recruitingPosts = postRepository.findAllByRecruitmentStateFalseOrderByCreateDateDesc();
        List<PostReadResponseDto> recrutingPostList = new ArrayList<>();

        for (Post post : recruitingPosts) {
            recrutingPostList.add(PostReadResponseDto.builder()
                    .status(StatusMessage.SUCCESS)
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .meetingType(post.getMeetingType())
                    .contact(post.getContact())
                    .period(post.getPeriod())
                    .recruitmentState(post.isRecruitmentState())
                    .hits(post.getHits())
                    .postDate(post.getCreateDate())
                    .writer(post.getMember().getNickname())
                    .build()
            );
        }
        return new ResponseEntity<>(recrutingPostList, HttpStatus.valueOf(StatusCode.SUCCESS));
    }

    // 조회수순 게시글 목록 조회 (모집 중인거만)
    public ResponseEntity<List<PostReadResponseDto>> readRecruitingHitsPostList() {
        List<Post> recruitingHitsPosts = postRepository.findAllByRecruitmentStateFalseOrderByHitsDesc();
        List<PostReadResponseDto> recrutingHitsPostList = new ArrayList<>();
        for (Post post : recruitingHitsPosts) {
            recrutingHitsPostList.add(PostReadResponseDto.builder()
                    .status(StatusMessage.SUCCESS)
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .meetingType(post.getMeetingType())
                    .contact(post.getContact())
                    .period(post.getPeriod())
                    .recruitmentState(post.isRecruitmentState())
                    .hits(post.getHits())
                    .postDate(post.getCreateDate())
                    .writer(post.getMember().getNickname())
                    .build()
            );
        }
        return new ResponseEntity<>(recrutingHitsPostList, HttpStatus.valueOf(StatusCode.SUCCESS));
    }

    // 조회수순 게시글 목록 조회 (모집 마감 포함)
    public ResponseEntity<List<PostReadResponseDto>> readHitsPostList() {
        List<Post> hitsPosts = postRepository.findAllByOrderByHitsDesc();
        List<PostReadResponseDto> hitsPostList = new ArrayList<>();
        for (Post post : hitsPosts) {
            hitsPostList.add(PostReadResponseDto.builder()
                    .status(StatusMessage.SUCCESS)
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .meetingType(post.getMeetingType())
                    .contact(post.getContact())
                    .period(post.getPeriod())
                    .recruitmentState(post.isRecruitmentState())
                    .hits(post.getHits())
                    .postDate(post.getCreateDate())
                    .writer(post.getMember().getNickname())
                    .build()
            );
        }
        return new ResponseEntity<>(hitsPostList, HttpStatus.valueOf(StatusCode.SUCCESS));
    }

    // 댓글 많은 순으로 게시글 목록 조회 (모집 중인거만)
    public ResponseEntity<List<PostReadResponseDto>> readRecruitingCommentsPostList() {
        List<Post> recruitingPosts = postRepository.findAllByRecruitmentStateFalse();
        getCommentsPosts(recruitingPosts);

        List<PostReadResponseDto> recruitCommentsPostList = new ArrayList<>();
        for (Post post : recruitingPosts) {
            recruitCommentsPostList.add(PostReadResponseDto.builder()
                    .status(StatusMessage.SUCCESS)
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .meetingType(post.getMeetingType())
                    .contact(post.getContact())
                    .period(post.getPeriod())
                    .recruitmentState(post.isRecruitmentState())
                    .hits(post.getHits())
                    .postDate(post.getCreateDate())
                    .writer(post.getMember().getNickname())
                    .build()
            );
        }
        return new ResponseEntity<>(recruitCommentsPostList, HttpStatus.valueOf(StatusCode.SUCCESS));
    }

    // 댓글 많은 순으로 게시글 목록 조회 (모집 마감 포함)
    public ResponseEntity<List<PostReadResponseDto>> readCommentsPostList() {
        List<Post> posts = postRepository.findAll();
        getCommentsPosts(posts);

        List<PostReadResponseDto> commentsPostList = new ArrayList<>();
        for (Post post : posts) {
            commentsPostList.add(PostReadResponseDto.builder()
                    .status(StatusMessage.SUCCESS)
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .meetingType(post.getMeetingType())
                    .contact(post.getContact())
                    .period(post.getPeriod())
                    .recruitmentState(post.isRecruitmentState())
                    .hits(post.getHits())
                    .postDate(post.getCreateDate())
                    .writer(post.getMember().getNickname())
                    .build()
            );
        }
        return new ResponseEntity<>(commentsPostList, HttpStatus.valueOf(StatusCode.SUCCESS));
    }

    // 댓글수 내림차순으로 게시글 정렬
    private void getCommentsPosts(List<Post> posts) {
        for (int i = 0; i< posts.size()-1; i++) {
            boolean swap = false;

            for (int j = 0; j< posts.size()-1-i; j++) {
                if(posts.get(j).getComments().size() < posts.get(j+1).getComments().size()) {
                    Collections.swap(posts, j, j+1);
                    swap = true;
                }
            }
            if (!swap) {
                break;
            }
        }
    }

    // 게시글 수정
    @Transactional
    public ResponseEntity<PostUpdateResponseDto> updatePost(Long postId, PostUpdateRequestDto requestDto, MemberDetails memberDetails) {
        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());
        Member member = memberOptional.get();
        Optional<Post> postOptional = member.findPost(postId);

        if (postOptional.isEmpty()) {
            log.error("nickname={}, error={}", member.getNickname(), "수정할 게시글이 존재하지 않음");
            return new ResponseEntity<>(
                    PostUpdateResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }
        Post findPost = postOptional.get();

        if (requestDto.getContent().length() > 2000) {
            log.error("nickname={}, error={}", member.getNickname(), "게시글 길이 2000자 초과");
            return new ResponseEntity<>(
                    PostUpdateResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }
        findPost.updatePost(requestDto);

        return new ResponseEntity<>(
                PostUpdateResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 게시글 삭제
    @Transactional
    public ResponseEntity<PostDeleteResponseDto> deletePost(Long postId, MemberDetails memberDetails) {
        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());
        Member member = memberOptional.get();

        boolean isValid = member.deletePost(postId);

        if(!isValid) {
            log.error("nickname={}, error={}", member.getNickname(), "삭제할 게시글이 존재하지 않음");
            return new ResponseEntity<>(
                    PostDeleteResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }
        postRepository.deleteById(postId);

        return new ResponseEntity<>(
                PostDeleteResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 관리자, 게시글 삭제 기능
    @Transactional
    public ResponseEntity<PostDeleteResponseDto> adminDeletePost(Long postId, MemberDetails memberDetails) {
        if (!memberDetails.getMember().getRole().equals(MemberRole.ADMIN)) {
            log.error("nickname={}, error={}", memberDetails.getNickname(), "관리자 권한이 없음");
            return new ResponseEntity<>(
                    PostDeleteResponseDto.builder().status(StatusMessage.FORBIDDEN_USER).build(),
                    HttpStatus.valueOf(StatusCode.FORBIDDEN_USER)
            );
        }

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            log.error("nickname={}, error={}", memberDetails.getNickname(), "해당 게시글이 존재하지 않음");
            return new ResponseEntity<>(
                    PostDeleteResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }
        Post findPost = postOptional.get();
        Member member = findPost.getMember();

        boolean isValid = member.deletePost(postId);

        if(!isValid) {
            log.error("nickname={}, error={}", memberDetails.getNickname(), "삭제할 게시글이 존재하지 않음");
            return new ResponseEntity<>(
                    PostDeleteResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }
        postRepository.deleteById(postId);

        return new ResponseEntity<>(
                PostDeleteResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 게시글 검색
    public ResponseEntity<List<PostReadResponseDto>> searchPost(String query) {
        List<Post> searchPosts = postRepository.findAllByTitleContainingOrderByCreateDateDesc(query);
        List<PostReadResponseDto> searchList = new ArrayList<>();
        for (Post post : searchPosts) {
            searchList.add(PostReadResponseDto.builder()
                    .status(StatusMessage.SUCCESS)
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .meetingType(post.getMeetingType())
                    .contact(post.getContact())
                    .period(post.getPeriod())
                    .recruitmentState(post.isRecruitmentState())
                    .hits(post.getHits())
                    .postDate(post.getLastModifiedDate())
                    .writer(post.getMember().getNickname())
                    .build()
            );
        }
        return new ResponseEntity<>(searchList, HttpStatus.valueOf(StatusCode.SUCCESS));
    }
}