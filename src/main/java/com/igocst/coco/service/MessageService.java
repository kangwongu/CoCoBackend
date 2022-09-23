package com.igocst.coco.service;

import com.igocst.coco.common.status.StatusCode;
import com.igocst.coco.common.status.StatusMessage;
import com.igocst.coco.domain.Member;
import com.igocst.coco.domain.Message;
import com.igocst.coco.dto.message.*;
import com.igocst.coco.repository.MemberRepository;
import com.igocst.coco.repository.MessageRepository;
import com.igocst.coco.security.MemberDetails;
import com.nhncorp.lucy.security.xss.XssPreventer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;

    // 쪽지 보내기
    @Transactional
    public ResponseEntity<MessageCreateResponseDto> createMessage(MessageCreateRequestDto messageCreateRequestDto, MemberDetails memberDetails) {

        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());
        Member sendMember = memberOptional.get();

        Optional<Member> receivedMemberOptional = memberRepository.findByNickname(messageCreateRequestDto.getReceiver());
        if(receivedMemberOptional.isEmpty()) {
            log.error("nickname={}, error={}", messageCreateRequestDto.getReceiver(), "쪽지 수신자가 존재하지 않음");
            return new ResponseEntity<>(
                    MessageCreateResponseDto.builder().status(StatusMessage.UNKNOWN_RECEIVER).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }

        // 본인에게 쪽지를 보낼 수 없음
        Member receivedMember = receivedMemberOptional.get();
        if(receivedMember == sendMember) {
            log.error("nickname={}, error={}", messageCreateRequestDto.getReceiver(), "쪽지 수신자가 발신자와 같음");
            return new ResponseEntity<>(
                    MessageCreateResponseDto.builder().status(StatusMessage.NOT_ALLOWED_RECEIVER).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }

        // 쪽지 내용 255자 제한
        if (messageCreateRequestDto.getContent().length() > 255) {
            log.error("nickname={}, error={}", sendMember.getNickname(), "쪽지 내용 255자 초과");
            return new ResponseEntity<>(
                    MessageCreateResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }
        // message 보내는 코드
        Message message = Message.builder()
                .receiver(receivedMember)
                .title(XssPreventer.escape(messageCreateRequestDto.getTitle()))
                .content(XssPreventer.escape(messageCreateRequestDto.getContent()))
                .build();

        sendMember.sendMessage(message);
        messageRepository.save(message);

        return new ResponseEntity<>(
                MessageCreateResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 받은 쪽지 상세 읽기
    @Transactional
    public ResponseEntity<MessageReadResponseDto> getMessage(Long messageId, MemberDetails memberDetails) {

        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());
        Member member = memberOptional.get();

        Optional<Message> messageOptional = member.findMessage(messageId);
        if (messageOptional.isEmpty()) {
            log.error("nickname={}, messageId={}, error={}", member.getNickname(), messageId, "해당 쪽지를 찾을 수 없음");
            return new ResponseEntity<>(
                    MessageReadResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }

        Message message = messageOptional.get();

        // 보낸 쪽지함에서 본인이 보낸 쪽지를 확인할 경우 읽음상태는 변경이 되지 않음
        // false -> true
        if (member != messageOptional.get().getSender()) { message.changeReadState();}

        return new ResponseEntity<>(
                MessageReadResponseDto.builder()
                        .id(message.getId())
                        .member(member.getNickname())
                        .sender(message.getSender().getNickname())
                        .title(message.getTitle())
                        .content(message.getContent())
                        .readState(message.isReadState())
                        .status(StatusMessage.SUCCESS)
                        .build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }

    // 쪽지 리스트 읽기
    // 받은 쪽지 리스트
    @Transactional
    public ResponseEntity<List<MessageListReadResponseDto>> getMessageList(@AuthenticationPrincipal MemberDetails memberDetails) {

        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());
        Member readMember = memberOptional.get();

        List<Message> messages = readMember.getReadMessage();

        List<MessageListReadResponseDto> messageList = new ArrayList<>();
        for (Message m : messages) {
            messageList.add(MessageListReadResponseDto.builder()
                    .id(m.getId())
                    .title(m.getTitle())
                    .sender(m.getSender().getNickname())
                    .readState(m.isReadState())
                    .createDate(m.getCreateDate())
                    .status(StatusMessage.SUCCESS)
                    .build());
        }
        return new ResponseEntity<>(messageList, HttpStatus.valueOf(StatusCode.SUCCESS));
    }

    // 보낸 쪽지 리스트
    @Transactional
    public ResponseEntity<List<MessageListReadResponseDto>> getSendMessageList(@AuthenticationPrincipal MemberDetails memberDetails) {
        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());

        List<Message> sendMessages = memberOptional.get().getSendMessage();
        List<MessageListReadResponseDto> sendMessageList = new ArrayList<>();
        for (Message m : sendMessages) {
            sendMessageList.add(MessageListReadResponseDto.builder()
                .id(m.getId())
                .title(m.getTitle())
                .receiver(m.getReceiver().getNickname())
                .readState(m.isReadState())
                .createDate(m.getCreateDate())
                .status(StatusMessage.SUCCESS)
                .build());
    }
        return new ResponseEntity<>(sendMessageList, HttpStatus.valueOf(StatusCode.SUCCESS));
}

    // 쪽지 삭제
    @Transactional
    public ResponseEntity<MessageDeleteResponseDto> deleteMessage(Long messageId, @AuthenticationPrincipal MemberDetails memberDetails) {

        Optional<Member> memberOptional = memberRepository.findById(memberDetails.getMember().getId());
        Member member = memberOptional.get();

        boolean isValid = member.deleteMessage(messageId);

        if (!isValid) {
            log.error("nickname={}, messageId={}, error={}", member.getNickname(), messageId, "해당 쪽지를 찾을 수 없음");
            return new ResponseEntity<>(
                    MessageDeleteResponseDto.builder().status(StatusMessage.BAD_REQUEST).build(),
                    HttpStatus.valueOf(StatusCode.BAD_REQUEST)
            );
        }
        messageRepository.deleteById(messageId);

        return new ResponseEntity<>(
                MessageDeleteResponseDto.builder().status(StatusMessage.SUCCESS).build(),
                HttpStatus.valueOf(StatusCode.SUCCESS)
        );
    }
}