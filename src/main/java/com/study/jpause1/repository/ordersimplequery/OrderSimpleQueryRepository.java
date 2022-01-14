package com.study.jpause1.repository.ordersimplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;
    public List<SimpleOrderQueryDto> findOrderDto() {
        return em.createQuery("select new com.study.jpause1.repository.ordersimplequery.SimpleOrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address) from Order o " +
                "join o.member m " +
                "join o.delivery d").getResultList();
    }
}
