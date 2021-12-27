package com.study.jpause1.repository;

import com.study.jpause1.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository
public class MemberRepository {

    // entity manager 주입, thread-safe 하지 않음, thread 마다, SharedEntityManagerCreator type의 프록시 inject 해준다
    // 실제 EntityManager는 싱글톤
    // 실제 EntityManager를 연결해주는 가짜 EntityManager를 주입, EntityManager만이 처리할 수 있는 문제를 만나게 되면 그 때 진짜 EntityManager객체에게 일을 위임
    @PersistenceContext
    private EntityManager em;

    /*
    // EntityFactory 주입 받는 법
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;
     */
    // persist시 영속성 context에 member 객체 넣는다
    public void save(Member member){
        em.persist(member);
    }
    public  Member findOne(Long id){
        return em.find(Member.class,id);
    }
    // jpql은 객체 대상으로
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name",Member.class)
                .setParameter("name",name)
                .getResultList();
    }
}
