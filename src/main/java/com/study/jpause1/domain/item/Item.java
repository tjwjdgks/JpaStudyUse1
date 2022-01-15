package com.study.jpause1.domain.item;

import com.study.jpause1.domain.Category;
import com.study.jpause1.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


// 상속 관계이기 때문에 상속 관계 전략을 지정해야한다 // 부모 클래스에 지정한다
/*
// toOne 관계일 때
@BatchSize(size = 100)
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name ="dtype") // 상속 구분자
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // 비즈니스 로직 // 정보 전문가 패턴 // 객체를 가지고 있는 곳에서 비즈니스 로직을 다룬다 // 응집력을 위해
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock<0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
