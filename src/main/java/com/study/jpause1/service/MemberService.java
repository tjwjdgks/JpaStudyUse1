package com.study.jpause1.service;

import com.study.jpause1.domain.Member;
import com.study.jpause1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// jpa의 모든 데이터 변경은 transaction 안에서 // public 메서드 transactional 들어간다 // spring transactional 추천
// 조회시 // 영속성 context flush 안함으로 dirty checking 이점이 있음 // db에게 hint 줄수 있음
@Transactional(readOnly = true)
@RequiredArgsConstructor // final 필드 생성자 만들어준다
public class MemberService {

    private final MemberRepository memberRepository;

    //회원 가입
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member); // 중복회원 검증
        // persist 할때(영속성 context에 등록될때), generateValue 일때 pk 값이 들어가 있는 것을 보장한다
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
    // 회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }
    // 단건 조회
    public Member findOne(Long id){
        return memberRepository.findOne(id);
    }
}
