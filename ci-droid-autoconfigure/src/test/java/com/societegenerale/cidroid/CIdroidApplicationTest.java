package com.societegenerale.cidroid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CIdroidApplicationTest {

    @Autowired
    private ApplicationContext appContext;

    @Test
    void appContextShouldLoad() {
        assertThat(appContext).isNotNull();
    }

}