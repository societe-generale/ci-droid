package com.societegenerale.cidroid;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CiDroidConfiguration.class)
public @interface EnableCiDroid {
}
