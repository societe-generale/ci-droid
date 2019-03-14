package com.societegenerale.cidroid.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    private static final String FORWARD_TO_INDEX = "forward:/index.html";

    @RequestMapping({
            "/",
            "/form"
    })
    public String index() {
        return FORWARD_TO_INDEX;
    }
}
