package com.igocst.coco.domain;

import com.igocst.coco.domain.timestamped.Timestamped;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends Timestamped{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_ID")
    private Long id;

    // 쪽지 보내는 회원
    @ManyToOne(fetch = FetchType.LAZY)
    private Member sender;

    // 쪽지 받는 회원
    @ManyToOne(fetch = FetchType.LAZY)
    private Member receiver;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean readState;

    @Builder
    public Message(Long id, Member sender, Member receiver, String title, String content, boolean readState) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.title = title;
        this.content = content;
        this.readState = readState;
    }

    public void sendMember(Member member) { this.sender = member;}
    public void changeReadState() { this.readState = true; }
}
