package com.study.jpause1.service;

import com.study.jpause1.domain.Address;
import com.study.jpause1.domain.Member;
import com.study.jpause1.domain.Order;
import com.study.jpause1.domain.OrderStatus;
import com.study.jpause1.domain.item.Book;
import com.study.jpause1.domain.item.Item;
import com.study.jpause1.exception.NotEnoughStockException;
import com.study.jpause1.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {
    // spring boot 가능 엔티티 메니저도 autowired 가능
    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    @Test
    @DisplayName("상품 주문")
    public void order() throws Exception{

        // given
        Member member = createMember("회원");

        Item book = createBook("ss", 1000, 10);

        int orderCount = 2;
        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER,getOrder.getStatus(),"상품 주문시 상태는 ORDER");
        assertEquals(1,getOrder.getOrderItems().size(),"주문한 상품 종류 수가 정확해야한다");
        assertEquals(1000*orderCount,getOrder.getTotalPrice(),"주문 가격은 가격 * 수량이다 ");
        assertEquals(8, book.getStockQuantity(),"주문 수량 만큼 재고가 줄어야 한다");
    }


    @Test
    @DisplayName("상품 주문 재고 수량 초과")
    public void overOrderStock() throws Exception{
        // given
        Member member = createMember("회원");
        Item item = createBook("ss", 1000, 10);

        int orderCount = 11;
        // when, then
        assertThrows(NotEnoughStockException.class,()->orderService.order(member.getId(),item.getId(),orderCount),"재고 수량 부족 예외가 발생해야 한다");
    }


    @Test
    @DisplayName("상품 취소")
    public void cancel() throws Exception{

        // given
        Member member = createMember("회원");
        Item book = createBook("ss", 1000, 10);

        int orderCount =2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL , getOrder.getStatus(),"주문 상태는 CANCEL");
        assertEquals(10, book.getStockQuantity(), "재고 증가해야 한다");


    }
    @Test
    @DisplayName("재고 수량 초과")
    public void checkRestStock() throws Exception{

    }

    private Member createMember(String name) {
        Member member= new Member();
        member.setName(name);
        member.setAddress(new Address("tt","tt","tt"));
        em.persist(member); // test 데이터를 단순히 넣기 위해
        return member;
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }
}