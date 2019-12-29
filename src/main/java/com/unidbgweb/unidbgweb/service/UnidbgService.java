package com.unidbgweb.unidbgweb.service;

public interface UnidbgService {
    String duEncodeByte(String v);

    String getAS(String deviceId);

    String kuaiShouSign(String deviceId);

    String xhsShield(String params, String sessionId, String deviceId, String agent);

    String getxPreAuthencode(String params);

    String getInfo2(String deviceId);
}
