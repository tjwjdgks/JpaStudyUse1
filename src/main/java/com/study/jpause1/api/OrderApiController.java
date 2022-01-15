package com.study.jpause1.api;

import com.study.jpause1.domain.Address;
import com.study.jpause1.domain.Order;
import com.study.jpause1.domain.OrderItem;
import com.study.jpause1.domain.OrderStatus;
import com.study.jpause1.repository.OrderRepository;
import com.study.jpause1.repository.OrderSearch;
import com.study.jpause1.repository.order.query.OrderFlatDto;
import com.study.jpause1.repository.order.query.OrderItemQueryDto;
import com.study.jpause1.repository.order.query.OrderQueryDto;
import com.study.jpause1.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    // Entity 직접 노출 비추천
    /*
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for(Order order : all ){
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o->o.getItem().getName());
        }
        return all;
    }
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return  collect;
    }
    // 모든 것을 fechjoin할 경우 db의 중복된 데이터까지 가져오기 때문에 애플리케이션의 중복데이터가 들어온다. 메모리 용량이 늘어난다
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        // 컬렉션 fetch join 단점- 메모리에서 페이징 처리를 한다 // 왜냐면 db에서 데이터가 늘어나므로 limit과 offset이 틀어지기 때문에
        // 컬렉션 페치 조인은 1개만 사용할 수 있다. 데이터가 부정합하게 조회 될 수 있다.
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return  collect;
    }
    // toOne 관계일때 fetch join
    // Collection 조회는 lazy loading 과 fetch size를 늘려서 한번에 가져온다
    // 페이지 가능하게 만들기
    // 쿼리는 v3 보다 늘어 났지만 최적화된 데이터가 들어온다. in(pk)로 다 가져온다
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit",defaultValue = "100") int limit){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o)).collect(toList());
        return  collect;
    }
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }
    // 쿼리 2번 최적화
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDto_optimization();
    }
    // 쿼리 1번
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6(){
        // 직접 중복을 걸러내야 한다
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());

    }

    @Getter
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;
        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems =  order.getOrderItems().stream().map(orderItem->new OrderItemDto(orderItem)).collect(toList());
        }
    }
    @Getter
    static class OrderItemDto{
        private String itemName;
        private int orderPirce;
        private int count;
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPirce  = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
