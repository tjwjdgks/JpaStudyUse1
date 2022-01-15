package com.study.jpause1;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Jpause1Application {

    public static void main(String[] args) {
        SpringApplication.run(Jpause1Application.class, args);
    }


    // 비추천 // Entity 바로 조회 // Entity를 바로 쓰기 위해서는 hibernate5Module 이 필요하다 // Jaskon은 프록시 객체를 못 읽는다
    /*
    @Bean
    Hibernate5Module hibernate5Module(){
        Hibernate5Module hibernate5Module = new Hibernate5Module();
        hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING,true);
        return hibernate5Module;
    }
     */
}
