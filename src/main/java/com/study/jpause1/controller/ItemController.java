package com.study.jpause1.controller;

import com.study.jpause1.domain.item.Book;
import com.study.jpause1.domain.item.Item;
import com.study.jpause1.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm bookForm){
        // 실무에서는 setter 사용 보다 정적 팩토리 메서드나 modelMapper 등 사용해서 객체 생성
        Book book = new Book();
        book.setName(bookForm.getName());
        book.setPrice(bookForm.getPrice());
        book.setAuthor(bookForm.getAuthor());
        book.setStockQuantity(bookForm.getStockQuantity());
        book.setIsbn(bookForm.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items",items);
        return "items/itemList";
    }
    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long id,Model model){
        Book book = (Book)itemService.findOne(id);

        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setName(book.getName());
        form.setStockQuantity(book.getStockQuantity());
        form.setPrice(book.getPrice());
        form.setAuthor(book.getAuthor());
        form.setIsbn(book.getIsbn());

        model.addAttribute("form",book);
        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@PathVariable(name = "itemId") Long id, @ModelAttribute(name = "form") BookForm form){
        // 식별자가 있음, db에 저장된 적이 있음 준영속 객체
        // persist 상태가 아니다
        Book book = new Book();
        book.setId(id);
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }
}
