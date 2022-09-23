package com.igocst.coco.service;

import com.igocst.coco.common.status.StatusCode;
import com.igocst.coco.common.status.StatusMessage;
import com.igocst.coco.domain.Comment;
import com.igocst.coco.domain.Member;
import com.igocst.coco.domain.MemberRole;
import com.igocst.coco.domain.Post;
import com.igocst.coco.dto.comment.CommentReadResponseDto;
import com.igocst.coco.dto.member.*;
import com.igocst.coco.dto.post.PostReadResponseDto;
import com.igocst.coco.repository.CommentRepository;
import com.igocst.coco.repository.MemberRepository;
import com.igocst.coco.repository.PostRepository;
import com.igocst.coco.s3.S3Service;
import com.igocst.coco.security.MemberDetails;
import com.igocst.coco.security.jwt.JwtTokenProvider;
import com.igocst.coco.util.FileUtils;
import com.nhncorp.lucy.security.xss.XssPreventer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${secret.admin.token}")
    private String ADMIN_TOKEN;

    // 로그인
    public ResponseEntity<LoginResponseDto> login(LoginRequestDto requestDto) {
        Optional<Member> memberOptional = memberRepository.findByEmail(requestDto.getEmail());
        if (memberOptional.isEmpty()) {
            log.error("error={}", "로그인 실패");
            return new ResponseEntity<>(
                    LoginResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }
        Member findMember = memberOptional.get();

        // BCrypt으로 암호화된 비밀번호를 비교
        if (!passwordEncoder.matches(requestDto.getPassword(), findMember.getPassword())) {
            log.error("nickname={}, error={}", findMember.getNickname(), "패스워드 오류");
            return new ResponseEntity<>(
                    LoginResponseDto.builder().status(StatusMessage.INVALID_PARAM).build(),
                    HttpStatus.valueOf(StatusCode.INVALID_PARAM)
            );
        }

        String token = jwtTokenProvider.generateToken(requestDto.getEmail());
        if (token == null) {
            log.error("nickname={}, error={}", findMember.getNickname(), "토큰 발급 오류");
            return new ResponseEntity<>(
                    LoginResponseDto.builder().status(StatusMessage.INVALID_TOKEN).build(),
                    HttpStatus.valueOf(StatusCode.INVALID_TOKEN)
            );
        }

        return new ResponseEntity<>(
                LoginResponseDto.builder().status(StatusMessage.SUCCESS).token(token).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 회원가입
    public ResponseEntity<RegisterResponseDto> register(RegisterRequestDto requestDto) {
        if (memberRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            log.error("error={}", "회원가입 시, 이메일 중복 오류");
            return new ResponseEntity<>(
                    RegisterResponseDto.builder().status(StatusMessage.DUPLICATED_USER).build(),
                    HttpStatus.valueOf(StatusCode.DUPLICATED_USER)
            );
        }

        if (memberRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            log.error("error={}", "회원가입 시, 닉네임 중복 오류");
            return new ResponseEntity<>(
                    RegisterResponseDto.builder().status(StatusMessage.DUPLICATED_USER).build(),
                    HttpStatus.valueOf(StatusCode.DUPLICATED_USER)
            );
        }

        MemberRole role = MemberRole.MEMBER;
        if (requestDto.isAdmin()) {
            if (!requestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                log.error("error={}", "유효하지 않은 관리자 인증 토큰");
                return new ResponseEntity<>(
                        RegisterResponseDto.builder().status(StatusMessage.INVALID_PARAM).build(),
                        HttpStatus.valueOf(StatusCode.INVALID_PARAM)
                );
            }
            role = MemberRole.ADMIN;
        }

        Member member = Member.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(XssPreventer.escape(requestDto.getNickname()))
                .profileImageUrl(requestDto.getProfileImageUrl())
                .githubUrl(requestDto.getGithubUrl())
                .portfolioUrl(requestDto.getPortfolioUrl())
                .introduction((requestDto.getIntroduction()))
                .role(role)
                .build();

        memberRepository.save(member);

        return new ResponseEntity<>(
                RegisterResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    //회원 정보 획득
    public ResponseEntity<MemberReadResponseDto> readMember(MemberDetails memberDetails) {
        Member member = memberDetails.getMember();

        return new ResponseEntity<>(
                MemberReadResponseDto.builder()
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .githubUrl(member.getGithubUrl())
                    .portfolioUrl(member.getPortfolioUrl())
                    .introduction(member.getIntroduction())
                    .profileImageUrl(member.getProfileImageUrl())
                    .status(StatusMessage.SUCCESS)
                    .build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 회원 정보 수정
    @Transactional
    public ResponseEntity<MemberUpdateResponseDto> updateMember(MemberUpdateRequestDto memberUpdateRequestDto,
                                                MemberDetails memberDetails) throws IOException {

        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());
        Member member = memberOptional.get();

        MultipartFile file = memberUpdateRequestDto.getFile();
        if (file != null) {
            InputStream inputStream = file.getInputStream();

            boolean isValid = FileUtils.validImgFile(inputStream);
            if(!isValid) {
                return new ResponseEntity<>(
                        MemberUpdateResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                        HttpStatus.valueOf(StatusCode.BAD_REQUEST));
            }
            else {
                String fileUrl = s3Service.upload(file, "profileImage", memberDetails);
                member.updateProfileImage(fileUrl);
            }
        }

        //그 멤버의 정보를 바꾼다.
        member.updateNickname(XssPreventer.escape(memberUpdateRequestDto.getNickname()));
        member.updateGithubUrl(XssPreventer.escape(memberUpdateRequestDto.getGithubUrl()));
        member.updatePortfolioUrl(XssPreventer.escape(memberUpdateRequestDto.getPortfolioUrl()));
        member.updateIntroduction(XssPreventer.escape(memberUpdateRequestDto.getIntroduction()));

        return new ResponseEntity<>(
                MemberUpdateResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 회원 탈퇴
    @Transactional
    public ResponseEntity<MemberDeleteResponseDto> deleteMember(MemberDetails memberDetails) {
        Member member = memberDetails.getMember();
        memberRepository.deleteById(member.getId());

        return new ResponseEntity<>(
                MemberDeleteResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 관리자, 회원 강제 탈퇴
    public ResponseEntity<MemberDeleteResponseDto> adminDeleteMember(Long userId) {
        memberRepository.deleteById(userId);

        return new ResponseEntity<>(
                MemberDeleteResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 이메일 중복 체크
    public ResponseEntity<CheckDupResponseDto> checkEmailDup(CheckEmailDupRequestDto checkEmailDupRequestDto) {
        String email = checkEmailDupRequestDto.getEmail();
        boolean isDup = memberRepository.findByEmail(email).isPresent();

        return new ResponseEntity<>(
                CheckDupResponseDto.builder().status(StatusMessage.SUCCESS).isDup(isDup).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 닉네임 중복 체크
    public ResponseEntity<CheckDupResponseDto> checkNicknameDup(CheckNicknameDupRequestDto checkNicknameDupRequestDto) {
        String nickname = checkNicknameDupRequestDto.getNickname();
        boolean isDup = memberRepository.findByNickname(nickname).isPresent();

        return new ResponseEntity<>(
                CheckDupResponseDto.builder().status(StatusMessage.SUCCESS).isDup(isDup).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 프로필 모달에서 닉네임 중복체크
    public ResponseEntity<CheckDupResponseDto>  checkNicknameDupProfile(CheckNicknameDupRequestDto checkNicknameDupRequestDto,
                                                                        MemberDetails memberDetails) {

        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());
        Member member = memberOptional.get();

        //닉네임이 기존 닉네임과 같지 않을 때 동작
        String nickname = checkNicknameDupRequestDto.getNickname();
        //중복이면 true, 아니면 false
        boolean isDup = false;

        if (memberRepository.findByNickname(nickname).isPresent()) {
            isDup = true;
        }

        if (nickname.equals(member.getNickname())) {
            isDup = false;
        }

        return new ResponseEntity<>(
                CheckDupResponseDto.builder().status(StatusMessage.SUCCESS).isDup(isDup).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 자신이 작성한 게시글 프로필에서 보여주기
    @Transactional
    public ResponseEntity<List<PostReadResponseDto>> readMyPosts(MemberDetails memberDetails) {
        List<Post> posts = postRepository.findAllByMember_IdOrderByCreateDateDesc(memberDetails.getMember().getId());
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

    public ResponseEntity<List<CommentReadResponseDto>> readMyComments(MemberDetails memberDetails) {
        List<Comment> comments = commentRepository.findAllByMember_IdOrderByLastModifiedDateDesc(memberDetails.getMember().getId());
        List<CommentReadResponseDto> commentList = new ArrayList<>();
        for (Comment comment : comments) {
            commentList.add(CommentReadResponseDto.builder()
                    .id(comment.getId())
                    .postId(comment.getPost().getId())
                    .comments(comment.getContent())
                    .nickname(comment.getMember().getNickname())
                    .createDate(comment.getCreateDate())
                    .status(StatusMessage.SUCCESS)
                    .build());
        }
        return new ResponseEntity<>(commentList, HttpStatus.valueOf(StatusCode.SUCCESS));
    }
}