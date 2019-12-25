package com.unidbgweb.unidbgweb.controller;

import com.unidbgweb.unidbgweb.service.UnidbgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnidbgController {
    @Autowired
    UnidbgService unidbgService;

    @GetMapping("/du")
    public String duEncodeByte(String params) {
        if (null == params) {
            return "参数不为空";
        }
        return unidbgService.duEncodeByte(params);
    }

    @GetMapping("/mfw")
    public String getxPreAuthencode(String params) {
        if (null == params) {
            return "params参数不为空";
        }
        return unidbgService.getxPreAuthencode(params);
    }

    @GetMapping("/kuan")
    public String getAs(String deviceId) {
        if (null == deviceId) {
            return "deviceId参数不为空";
        }
        return unidbgService.getAS(deviceId);
    }

    @GetMapping("/pdd")
    public String getInfo2(String deviceId) {
        if (null == deviceId) {
            return "deviceId参数不为空";
        }
        return unidbgService.getInfo2(deviceId);
    }

    @GetMapping("/xhs")
    public String xhsShield(String params, String sessionId, String deviceId) {
        if (null == deviceId || null == params || null == sessionId) {
            return "params、deviceId、sessionId参数不为空";
        }
        return unidbgService.xhsShield(params, sessionId, deviceId);
    }

}
