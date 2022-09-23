package com.igocst.coco.service;

import com.igocst.coco.common.status.StatusCode;
import com.igocst.coco.common.status.StatusMessage;
import com.igocst.coco.domain.Comment;
import com.igocst.coco.domain.Member;
import com.igocst.coco.domain.MemberRole;
import com.igocst.coco.domain.Post;
import com.igocst.coco.dto.comment.*;
import com.igocst.coco.repository.CommentRepository;
import com.igocst.coco.repository.MemberRepository;
import com.igocst.coco.repository.PostRepository;
import com.igocst.coco.security.MemberDetails;
import com.nhncorp.lucy.security.xss.XssPreventer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 댓글 생성
    @Transactional
    public ResponseEntity<CommentCreateResponseDto> createComment(CommentCreateRequestDto commentCreateRequestDto, Long postId, MemberDetails memberDetails) { //join = comment create
        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());
        Member member = memberOptional.get();

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            log.error("nickname={}, error={}", member.getNickname(), "해당 게시글을 찾을 수 없음");
            return new ResponseEntity<>(
                    CommentCreateResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }
        Post post = postOptional.get();

        if (commentCreateRequestDto.getContent().length() > 255) {
            log.error("nickname={}, error={}", member.getNickname(), "댓글 글자 255자 초과");
            return new ResponseEntity<>(
                    CommentCreateResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }

        Comment comment = Comment.builder()
                .content(XssPreventer.escape(commentCreateRequestDto.getContent()))
                .build();

        member.createComment(comment);
        post.createComment(comment);

        commentRepository.save(comment);

        return new ResponseEntity<>(
                CommentCreateResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 댓글 조회
    public ResponseEntity<List<CommentReadResponseDto>> readCommentList(Long postId, MemberDetails memberDetails) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        List<CommentReadResponseDto> output = new ArrayList<>();
        boolean enableDelete;
        boolean enableUpdate;

        for(Comment c : comments) {
            enableDelete = false;
            enableUpdate = false;

            if (c.getMember().getId() == memberDetails.getMember().getId()) {
                enableDelete = true;
                enableUpdate = true;
            }

            MemberRole memberRole = MemberRole.MEMBER;
            if (memberDetails.getMember().getRole().equals(MemberRole.ADMIN)) {
                memberRole = MemberRole.ADMIN;
                enableDelete = true;
            }

            output.add(CommentReadResponseDto.builder()
                    .id(c.getId())
                    .comments(c.getContent())
                    .nickname(c.getMember().getNickname())
                    .profileImageUrl(c.getMember().getProfileImageUrl())
                    .githubUrl(c.getMember().getGithubUrl())
                    .portfolioUrl(c.getMember().getPortfolioUrl())
                    .introduction(c.getMember().getIntroduction())
                    .createDate(c.getCreateDate())
                    .modifyDate(c.getLastModifiedDate())
                    .status(StatusMessage.SUCCESS)
                    .enableDelete(enableDelete)
                    .enableUpdate(enableUpdate)
                    .memberRole(memberRole)
                    .build());
        }
        return new ResponseEntity<>(output, HttpStatus.valueOf(StatusCode.SUCCESS));
    }

    //댓글 수정
    @Transactional
    public ResponseEntity<CommentUpdateResponseDto> updateComment(CommentUpdateRequestDto commentUpdateRequestDto,
                                                  Long commentId, MemberDetails memberDetails) {
        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());
        Member member = memberOptional.get();

        Optional<Comment> commentOptional = member.findComment(commentId);
        if (commentOptional.isEmpty()) {
            log.error("nickname={}, error={}", member.getNickname(), "해당 댓글을 찾을 수 없습니다.");
            return new ResponseEntity<>(
                    CommentUpdateResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }

        if (commentUpdateRequestDto.getContent().length() > 255) {
            log.error("nickname={}, error={}", member.getNickname(), "댓글 수정 글자 255자 초과");
            return new ResponseEntity<>(
                    CommentUpdateResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }
        Comment comment = commentOptional.get();
        comment.updateContent(commentUpdateRequestDto.getContent());

        return new ResponseEntity<>(
                CommentUpdateResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    //댓글 삭제
    @Transactional
    public ResponseEntity<CommentDeleteResponseDto> deleteComment(Long id, MemberDetails memberDetails) {
        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());
        Member member = memberOptional.get();

        boolean isValid = member.deleteComment(id);
        if(!isValid) {
            log.error("nickname={}, error={}", member.getNickname(), "댓글을 찾을 수 없습니다.");
            return new ResponseEntity<>(
                    CommentDeleteResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }
        commentRepository.deleteById(id);

        return new ResponseEntity<>(
                CommentDeleteResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 관리자, 댓글 삭제
    public ResponseEntity<CommentDeleteResponseDto> adminDeleteComment(Long commentId, MemberDetails memberDetails) {
        if (!memberDetails.getMember().getRole().equals(MemberRole.ADMIN)) {
            log.error("nickname={}, error={}", memberDetails.getNickname(), "관리자 권한이 없음");
            return new ResponseEntity<>(
                    CommentDeleteResponseDto.builder().status(StatusMessage.FORBIDDEN_USER).build(),
                    HttpStatus.valueOf(StatusCode.FORBIDDEN_USER)
            );
        }
        commentRepository.deleteById(commentId);

        return new ResponseEntity<>(
                CommentDeleteResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }
}