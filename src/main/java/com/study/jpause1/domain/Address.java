package com.study.jpause1.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

// jpa 내장 type
// 값 type은 변경이 되면 안됨
// 생성 할때 만 값이 설정 되는 것이 좋음
@Embeddable // 어딘가에 내장 될 수 있다
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    // jpa 스펙에서는 protected 까지 허용해준다
    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
