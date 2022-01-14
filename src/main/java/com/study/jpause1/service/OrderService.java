package com.study.jpause1.service;

import com.study.jpause1.domain.Delivery;
import com.study.jpause1.domain.Member;
import com.study.jpause1.domain.Order;
import com.study.jpause1.domain.OrderItem;
import com.study.jpause1.domain.item.Item;
import com.study.jpause1.repository.ItemRepository;
import com.study.jpause1.repository.MemberRepository;
import com.study.jpause1.repository.OrderRepository;
import com.study.jpause1.repository.OrderSearch;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    // 주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 설정
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);

        return order.getId();
    }

    // 취소

    @Transactional
    public void cancelOrder(Long orderId){
        // 단순히 order를 orderRepository로 조회를 하게 되면 order와 연관된 모든 엔티티들이 영속상태가 된다.
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
    }

    // 검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}
