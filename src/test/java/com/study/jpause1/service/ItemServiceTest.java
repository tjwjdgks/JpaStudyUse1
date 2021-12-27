package com.study.jpause1.service;

import com.study.jpause1.domain.item.Book;
import com.study.jpause1.domain.item.Item;
import com.study.jpause1.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("item 생성")
    public void createItem() throws Exception{
        Item item = new Book();
        item.setName("test");
        itemRepository.save(item);

        assertEquals(item,itemRepository.findOne(item.getId()));
    }
}