package com.igocst.coco.security;

import com.igocst.coco.domain.Member;
import com.igocst.coco.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public MemberDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        if (memberOptional.isEmpty()) {
            return null;
        }
        Member member = memberOptional.get();

        // DB에 일치하는 회원이 있으면 MemberDetails로 보냄
        return new MemberDetails(member);
    }
}