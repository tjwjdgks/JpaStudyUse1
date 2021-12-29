package com.study.jpause1.repository;

import com.study.jpause1.domain.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 동적 쿼리
@Getter @Setter
public class OrderSearch {
    // 이러한 조건이 있으면 where로 검색되어야 한다
    private String memberName; // 회원 이름
    private OrderStatus orderStatus; // 주문 상태

}
