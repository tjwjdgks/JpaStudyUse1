package com.study.jpause1.repository;

import com.study.jpause1.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor // spring jpa 의 기능 Entity 매니저 주입 가능
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        // 새로 생성한 객체
        if(item.getId() == null){
            em.persist(item);
        }
        // 영속성 context 관리 detached => persist
        else{
            em.merge(item);
        }
    }
    public Item findOne(Long id){
        return em.find(Item.class,id);
    }
    public List<Item> findAll(){
        return em.createQuery("select i from Item i",Item.class)
                .getResultList();
    }
}
