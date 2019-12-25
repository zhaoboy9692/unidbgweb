package com.unidbgweb.unidbgweb.service;

public interface UnidbgService {
    String duEncodeByte(String v);

    String getAS(String deviceId);

    String xhsShield(String params, String sessionId, String deviceId);

    String getxPreAuthencode(String params);

    String getInfo2(String deviceId);
}
