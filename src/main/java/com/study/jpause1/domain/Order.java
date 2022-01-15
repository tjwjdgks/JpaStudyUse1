package com.study.jpause1.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
// 생성 메서드 유도 // 기본 생성자 못쓰게 // 기본 생성자가 protected로 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id") // 외래키 매핑할 때 사용, 생략할 수 있음
    private Member member;

    @BatchSize(size = 1000) // 세부적으로 적용하고 싶을 때 // Collection 일때
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id") // 조회를 많이 하는 쪽이 주인 // Order가 주인
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 {ORDER, CANCEL}

    // == 연관 관계 편의 메서드 ==
    // db에 저장이 되기위해서는 주인에만 저장해도 되지만 트랜잭션이나 양방향 연관관계를 생각했을 때 양쪽에 데이터 넣는 것이 좋다.
    // 메서드의 위치는 핵심적으로 관리하는 족이 좋다
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }


    //== 생성 메서드 ==// 복잡한 생성은 생성 메서드를 통해
    public static Order createOrder(Member member, Delivery delivery, OrderItem ... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem item : orderItems)
            order.addOrderItem(item);

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //== 비즈니스 로직 ==//
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능 합니다");
        }
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem item : orderItems){
            item.cancel();
        }
    }
    //== 조회 로직 == //
    public int getTotalPrice(){
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    }
}
