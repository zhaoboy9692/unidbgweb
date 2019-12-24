package com.unidbgweb.unidbgweb.controller;

import com.unidbgweb.unidbgweb.service.UnidbgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnidbgController {
    @Autowired
    UnidbgService unidbgService;

    @RequestMapping("/du")
    public String duEncodeByte() {
        return unidbgService.duEncodeByte();
    }

    @RequestMapping("/kuan")
    public String getAs() {
        return unidbgService.getAS();
    }

    @RequestMapping("/xhs")
    public String xhsShield() {
        return unidbgService.xhsShield();
    }

}
