package com.unidbgweb.unidbgweb.serviceImp;


import com.unidbgweb.unidbgweb.service.UnidbgService;
import com.unidbgweb.unidbgweb.unidbg.KuAnAS;
import com.unidbgweb.unidbgweb.unidbg.duEncodeByte;
import com.unidbgweb.unidbgweb.unidbg.xhsShield;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service("UnidbgService")
public class UnidbgServiceImp implements UnidbgService {
    private static duEncodeByte duEncodeByte;
    private static xhsShield xhsShield;
    private static KuAnAS kuAnAS;

    static {
        try {
            duEncodeByte = new duEncodeByte();
            xhsShield = new xhsShield();
            kuAnAS = new KuAnAS();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized String duEncodeByte() {
        //毒app 4.16.0
        String value = "123456";//要加密的值
        return duEncodeByte.encodeByte(value);
    }

    @Override
    public synchronized String getAS() {
        //酷安
        return kuAnAS.getAS();
    }

    @Override
    public synchronized String xhsShield() {
        //小红书
        String params = "channel=YingYongBaodeviceId=ec258cae-33c4-35ca-a909-67bf45c0f73edevice_fingerprint=20191123001415aae06280fc655b5c963e9db249e364ce0160af67ab7f3c61device_fingerprint1=20191123001415aae06280fc655b5c963e9db249e364ce0160af67ab7f3c61filters=[]keyword=做菜1lang=zh-Hanspage=1page_size=20platform=Androidsearch_id=EED2BE2760DAA985FCA3CE9DA636172Asid=session.1569218578701927068721sign=de46015c85b29d8cff44c3fd1535b28asort=source=search_result_notest=1575460839url=/api/sns/v8/search/notesversionName=5.26.0";
        String sessionId = "session.1576053277833681415997";
        String deviceId = "b69a9d77-5e0c-3341-beef-b8c40fde38a6";
        String userAgent = "Dalvik/2.1.0 (Linux; U; Android 6.0.1; Nexus 5 Build/M4B30Z) Resolution/1080*1776 Version/5.26.0 Build/5260254 Device/(LGE;Nexus 5) NetType/WiFi";
        String algorithm = "S3";//S1、S2、S3、S4 4种算法
        return xhsShield.getShield(params, sessionId, deviceId, userAgent, algorithm);
    }
}
