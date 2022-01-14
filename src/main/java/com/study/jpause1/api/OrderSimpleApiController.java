package com.study.jpause1.api;

import com.study.jpause1.domain.Address;
import com.study.jpause1.domain.Order;
import com.study.jpause1.domain.OrderStatus;
import com.study.jpause1.repository.OrderRepository;
import com.study.jpause1.repository.OrderSearch;
import com.study.jpause1.repository.ordersimplequery.OrderSimpleQueryRepository;
import com.study.jpause1.repository.ordersimplequery.SimpleOrderQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * xToOne 성능 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    // 엔티티를 노출하기 때문에 비추천 방법 // lazy n+1 문제 발생
    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1(){
        // Order안에 Member가 있으므로, Order -> Member // Member안에 List<Order> 가 있으므로, Member -> Order // 이 과정이 계속 반복되서 무한 루프에 빠진다
        // 양방향 연관관계 문제 생김 // 양방향 연관관계에서 한 쪽은 @JsonIgnore 해주어야 함
        // Order에서 member가 lazy이므로 proxy Member가 들어가 있어 jackson 라이브러리에서 오류 난다
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for(Order order : all){
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }
    // n+1 문제 발생
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        return orders.stream()
                .map(o->new SimpleOrderDto(o))
                .collect(toList());
    }

    // n+1 문제 해결 // fetch join 사용
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream()
                .map(o->new SimpleOrderDto(o))
                .collect(toList());
    }
    // dto로 바로 조회 // 성능은 나아지지만 재사용성이 떨어지고 repository가 view에 의존적으로 된다
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> ordersV4(){
        return orderSimpleQueryRepository.findOrderDto();
    }
    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
