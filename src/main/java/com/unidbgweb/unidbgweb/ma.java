package com.unidbgweb.unidbgweb;

public class ma {
    public static byte[] hexStringToByteArray(String str) {
        String str2 = str;
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str2.charAt(i), 16) << 4) + Character.digit(str2.charAt(i + 1), 16));
        }
        return bArr;
    }

    public static String format_url(String str) {
        int indexOf = str.indexOf("?");
        int indexOf2 = str.indexOf("#");
        if (indexOf == -1) {
            return null;
        }
        if (indexOf2 == -1) {
            return str.substring(indexOf + 1);
        }
        if (indexOf2 < indexOf) {
            return null;
        }
        return str.substring(indexOf + 1, indexOf2);
    }

    public static void main(String[] args) {
        //ts是十位时间戳1577370985
//使用的是这个方法 第一位ts
//第二位是 https://lf.snssdk.com/article/v4/tab_comments/?group_id=6774541433287213580&item_id=6774541433287213580&aggr_type=1&count=20&offset=0&tab_index=0&fold=1&iid=97008549225&device_id=70191588300&ac=wifi&channel=wandoujia2&aid=13&app_name=news_article&version_code=707&version_name=7.0.7&device_platform=android&ab_version=662176%2C801968%2C707372%2C1390313%2C668775%2C1392595%2C1370518%2C1375661%2C1190523%2C1394633%2C668779%2C1251923%2C662099%2C1353775%2C1352824%2C668774%2C1197636%2C1388894%2C765197%2C857804%2C660830%2C1054755%2C1230781%2C1362835%2C1243993%2C759657&ab_client=a1%2Cc4%2Ce1%2Cf1%2Cg2%2Cf7&ab_group=100170%2C94564%2C102752%2C181430&ab_feature=94564%2C102752&abflag=3&ssmix=a&device_type=OPPO+R11&device_brand=OPPO&language=zh&os_api=23&os_version=6.0&uuid=862119037352072&openudid=7337c8189240625&manifest_version_code=707&resolution=1080
//第三位是 iid,97008549225,device_id,70191588300,ac,wifi,channel,wandoujia2,aid,13,app_name,news_article,version_code,707,version_name,7.0.7,device_platform,android,ab_version,662176,801968,707372,1390313,668775,1392595,1370518,1375661,1190523,1394633,668779,1251923,662099,1353775,1352824,668774,1197636,1388894,765197,857804,660830,1054755,1230781,1362835,1243993,759657,ab_client,a1,c4,e1,f1,g2,f7,ab_group,100170,94564,102752,181430,ab_feature,94564,102752,abflag,3,ssmix,a,device_type,OPPO R11,device_brand,OPPO,language,zh,os_api,23,os_version,6.0,uuid,862119037352072,openudid,7337c8189240625,manifest_version_code,707,resolution,1080*1812,dpi,480,update_version_code,70714,plugin,26958,tma_jssdk_version,1.10.6.5,pos,5r_88Pzt3vTp5L-nv3sqLHgBLnglH7-xv_zw_O3R8vP69Ono-fi_p6ysrrOkrq-rqa6xv_zw_O3R_On06ej5-L-nr6-zq6qlrKqs4A==,rom_version,coloros__bln-al10c00b172
//as 结果是44位，取一半，也就是22位
        String uif = "a2150cf04968ae65046233c58eef5996470051e2KoSw";
        String as = uif.substring(0, uif.length() >> 1);
        System.out.println();
        //mas 如下
        //native res = com.ss.sys.ces.a.e(as.getBytes())
        //mas = DigestUtils.toHexString(res)


        //x-go分析com.ss.sys.ces.a.leviathan();
        int x_khronos = (int) (System.currentTimeMillis() / 1000);
        String access$100 = format_url("传url");
        String X_Gorgon = byteArrayToHexStr(leviathan(x_khronos, hexStringToByteArray(md5(access$100) + "000的那几个" + md5("cookie") + "0000的那几个")));
    }

    private static native byte[] leviathan(int x_khronos, byte[] cookies);

    private static String md5(String access$100) {
        return "";
    }

    private static String byteArrayToHexStr(byte[] bArr2) {
        if (bArr2 == null) {
            return null;
        } else {
            char[] charArray = "0123456789abcdef".toCharArray();
            char[] cArr = new char[(bArr2.length * 2)];
            for (int i = 0; i < bArr2.length; i++) {
                int b2 = (bArr2[i] & 255);
                int i2 = i * 2;
                cArr[i2] = charArray[b2 >>> 4];
                cArr[i2 + 1] = charArray[b2 & 15];
            }
            return new String(cArr);
        }
    }

}
