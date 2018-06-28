package com.societegenerale.cidroid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings("squid:S1118") //can't add a private constructor, otherwise app won't start
public class CIdroidApplication {

    @SuppressWarnings("squid:S2095")
    public static void main(String[] args) {
        SpringApplication.run(CIdroidApplication.class, args);
    }
}
