package com.study.jpause1.controller;

import com.study.jpause1.domain.Member;
import com.study.jpause1.domain.Order;
import com.study.jpause1.domain.item.Item;
import com.study.jpause1.repository.OrderRepository;
import com.study.jpause1.repository.OrderSearch;
import com.study.jpause1.service.ItemService;
import com.study.jpause1.service.MemberService;
import com.study.jpause1.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final ItemService itemService;
    private final MemberService memberService;
    private final OrderRepository orderRepository;
    @GetMapping("/order")
    public String createForm(Model model){

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members",members);
        model.addAttribute("items",items);

        return "order/orderForm";
    }
    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId, @RequestParam("itemId") Long itemId, @RequestParam("count") int count, RedirectAttributes attributes){
        Long order = orderService.order(memberId, itemId, count);
        attributes.addFlashAttribute("order",orderRepository.findOne(order));
        return "redirect:/orders";
    }
    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model){
        Order order =(Order) model.getAttribute("order");
        if(order != null){
            orderSearch.setOrderStatus(order.getStatus());
            orderSearch.setMemberName(order.getMember().getName());
        }
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders",orders);
        return "order/orderList";
    }
    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
