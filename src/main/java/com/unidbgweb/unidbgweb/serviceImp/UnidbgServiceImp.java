package com.unidbgweb.unidbgweb.serviceImp;


import com.unidbgweb.unidbgweb.service.UnidbgService;
import com.unidbgweb.unidbgweb.unidbg.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("UnidbgService")
public class UnidbgServiceImp implements UnidbgService {
    private static duEncodeByte duEncodeByte;
    private static xhsShield xhsShield;
    private static KuAnAS kuAnAS;
    private static AuthorizeHelper authorizeHelper;
    private static DeviceNative deviceNative;
    private static KuaiShouSign kuaiShouSign;

    static {
        try {
            duEncodeByte = new duEncodeByte();
            xhsShield = new xhsShield();
            kuAnAS = new KuAnAS();
            authorizeHelper = new AuthorizeHelper();
            deviceNative = new DeviceNative();
            kuaiShouSign = new KuaiShouSign();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized String duEncodeByte(String value) {
        //毒app 4.16.0
        return duEncodeByte.encodeByte(value);
    }

    @Override
    public synchronized String getAS(String deviceId) {
        //酷安
        return kuAnAS.getAS(deviceId);
    }

    @Override
    public synchronized String getxPreAuthencode(String params) {
//        String str = "GET&https%3A%2F%2Fm.mafengwo.cn%2Fnb%2Fnotify%2Freg.php&app_code%3Dcom.mfw.roadbook%26app_ver%3D9.3.7%26app_version_code%3D734%26brand%3Dxiaomi%26channel_id%3DMFW%26dev_ver%3DD1907.0%26device_id%3D00%253A81%253A3b%253A8c%253Ac4%253Afb%26device_type%3Dandroid%26getui_cid%3Ded2298df7a84b5cfba0bda07f0941e17%26getui_errorcode%3D0%26hardware_model%3Dxiaomi%25206%26has_notch%3D0%26mfwsdk_ver%3D20140507%26oauth_consumer_key%3D5%26oauth_nonce%3D1e42255c-2d49-4dd2-be63-fc139c7ee4da%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1558012774%26oauth_token%3D32292063_a78f3be14db160e12118b1fe0ec11219%26oauth_version%3D1.0%26open_udid%3D00%253A81%253A3b%253A8c%253Ac4%253Afb%26patch_ver%3D3.0%26push_open%3D1%26screen_height%3D960%26screen_scale%3D1.5%26screen_width%3D540%26sys_ver%3D5.1.1%26time_offset%3D480%26uid%3D32292063%26x_auth_mode%3Dclient_auth";
        return authorizeHelper.getxPreAuthencode(params);
    }

    @Override
    public String getInfo2(String deviceId) {
        //拼多多Id
        //353490069873368
        return deviceNative.getInfo2(deviceId);
    }

    @Override
    public synchronized String xhsShield(String params, String sessionId, String deviceId, String userAgent) {
        //小红书
//        String params = "channel=YingYongBaodeviceId=ec258cae-33c4-35ca-a909-67bf45c0f73edevice_fingerprint=20191123001415aae06280fc655b5c963e9db249e364ce0160af67ab7f3c61device_fingerprint1=20191123001415aae06280fc655b5c963e9db249e364ce0160af67ab7f3c61filters=[]keyword=做菜1lang=zh-Hanspage=1page_size=20platform=Androidsearch_id=EED2BE2760DAA985FCA3CE9DA636172Asid=session.1569218578701927068721sign=de46015c85b29d8cff44c3fd1535b28asort=source=search_result_notest=1575460839url=/api/sns/v8/search/notesversionName=5.26.0";
//        String sessionId = "session.1576053277833681415997";
//        String deviceId = "b69a9d77-5e0c-3341-beef-b8c40fde38a6";
//        String userAgent = "Dalvik/2.1.0 (Linux; U; Android 6.0.1; Nexus 5 Build/M4B30Z) Resolution/1080*1776 Version/5.26.0 Build/5260254 Device/(LGE;Nexus 5) NetType/WiFi";
        String algorithm = "S1";//S1、S2、S3、S4 4种算法
        return xhsShield.getShield(params, sessionId, deviceId, userAgent, algorithm);
    }
    @Override
    public synchronized String kuaiShouSign(String params) {
        //小红书
//        String params = "channel=YingYongBaodeviceId=ec258cae-33c4-35ca-a909-67bf45c0f73edevice_fingerprint=20191123001415aae06280fc655b5c963e9db249e364ce0160af67ab7f3c61device_fingerprint1=20191123001415aae06280fc655b5c963e9db249e364ce0160af67ab7f3c61filters=[]keyword=做菜1lang=zh-Hanspage=1page_size=20platform=Androidsearch_id=EED2BE2760DAA985FCA3CE9DA636172Asid=session.1569218578701927068721sign=de46015c85b29d8cff44c3fd1535b28asort=source=search_result_notest=1575460839url=/api/sns/v8/search/notesversionName=5.26.0";
//        String sessionId = "session.1576053277833681415997";
//        String deviceId = "b69a9d77-5e0c-3341-beef-b8c40fde38a6";
//        String userAgent = "Dalvik/2.1.0 (Linux; U; Android 6.0.1; Nexus 5 Build/M4B30Z) Resolution/1080*1776 Version/5.26.0 Build/5260254 Device/(LGE;Nexus 5) NetType/WiFi";
        String algorithm = "S1";//S1、S2、S3、S4 4种算法
        return kuaiShouSign.getSign(params);
    }
}
