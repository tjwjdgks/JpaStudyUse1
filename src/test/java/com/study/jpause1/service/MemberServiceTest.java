package com.study.jpause1.service;

import com.study.jpause1.domain.Member;
import com.study.jpause1.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 같은 transcation 안에서 id가 같으면, 같은 영속성 context에서 똑같은 객체
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("회원가입")
    public void createUser() throws Exception{
        // given
        Member member = new Member();
        member.setName("kim");
        // when
        Long saveId = memberService.join(member);
        // then
        em.flush(); // 강제로 flush
        assertEquals(member,memberRepository.findOne(saveId));
    }
    @Test
    @DisplayName("중복 회원 확인")
    public void checkDuplicateUser() throws Exception{
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);
        // then
        assertThrows(IllegalStateException.class,()->memberService.join(member1));
    }
}