package com.study.jpause1.domain;



import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded // 내장 type을 포함했다
    private Address address;

    @OneToMany(mappedBy = "member") // Order 테이블에 있는 member 필드가 주인이다
    private List<Order> orders = new ArrayList<>();
}
