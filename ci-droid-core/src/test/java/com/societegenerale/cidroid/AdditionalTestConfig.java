package com.societegenerale.cidroid;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class  AdditionalTestConfig{

    @Bean
    public CiDroidProperties ciDroidProperties() {
        return new CiDroidProperties(new ArrayList<>(),new ArrayList<>());
    }

}
