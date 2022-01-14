package com.study.jpause1.domain;



import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

// 엔티티는 화면을 뿌리기위한 로직이 들어가서는 안된다
@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded // 내장 type을 포함했다
    private Address address;


    // @JsonIgnore // json으로 공개하고 싶지 않을 때 사용
    @OneToMany(mappedBy = "member") // Order 테이블에 있는 member 필드가 주인이다
    private List<Order> orders = new ArrayList<>();
}
