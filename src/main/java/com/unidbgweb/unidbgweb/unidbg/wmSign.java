package com.unidbgweb.unidbgweb.unidbg;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class wmSign {
    public static String a(String str, String str2, long j, int i) {
        if (str.charAt(str.length() - 1) != '/') {
            str = str + "/";
        }
        return a(str + str2 + "/" + j + "/" + i);
    }

    private static String a(String str) {
        byte[] bArr;
        try {
            BigInteger bigInteger = new BigInteger("AC14E4A51F1B8E11A95971CA4EBD7E2314631F137596A66A43FA2D792B2FD8447CBD6553D591F00A8B9D58E8BA33C229317A0D122C965D84A286114A963C3AE2694C81665D5AF04C80A71CBF350CD4C66280DC8FADBE6B8EDA7B2EC3D0C50E150850445EF84D3A4192662AC135D912C2CA2C68176D79EC64CACFF34089482B69", 16);
            BigInteger bigInteger2 = new BigInteger("010001", 16);
            Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            instance.init(1, KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(bigInteger, bigInteger2)));
            byte[] bytes = str.getBytes();
            int length = bytes.length;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i = 0;
            int i2 = 0;
            while (true) {
                int i3 = length - i;
                if (i3 > 0) {
                    if (i3 > 117) {
                        bArr = instance.doFinal(bytes, i, 117);
                    } else {
                        bArr = instance.doFinal(bytes, i, i3);
                    }
                    byteArrayOutputStream.write(bArr, 0, bArr.length);
                    i2++;
                    i = i2 * 117;
                } else {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    return Base64.getEncoder().encodeToString(byteArray);
                }
            }

        } catch (Exception e7) {
            return "";
        }
    }

    private static String startCollection() {

        String sb = "1.2}}" +
                getHWProperty() +
                "}}" +
                getHWStatus() +
                "}}" +
                getHWEquipmentInfo() +
                "}}" +
                getExternalEquipmentInfo() +
                "}}" +
                getUserAction() +
                "}}" +
                getEnvironmentInfo() +
                "}}" +
                getPlatformInfo() +
                "}}" +
                getLocationInfo() +
                "}}";
        return sb.replaceAll("null", "-").replaceAll("unknown", "-");
    }

    private static String getLocationInfo() {
        //CMCC-gFRr wifif名字
        //84:74:60:03:f4:b8 mac地址
        return "0.0|0.0|CMCC-gFRr|84:74:60:03:f4:b8|1|-56|-|-|-|";
    }

    private static String getPlatformInfo() {
        return "Android|com.sankuai.meituan.takeoutnew|7.23.4|23|-|2020-02-07 16:33:24:024|219|63628219|3634810880|3001114624|1581059831998|null|null";
    }

    private static String getEnvironmentInfo() {
        return "1|0|0|AAAA|1|0|0.3529412|66|false|0";
    }

    private static String getUserAction() {
        return "-|0|1|0|0|-|b9f2ab3d-7369-4bca-b40e-304d527ebcec|0";
    }

    private static String getExternalEquipmentInfo() {
        return "866963024025332|null|-|1920*1080|12GB|12GB|02:00:00:00:00:00||wifi";
    }

    private static String getHWEquipmentInfo() {
        return "ARMv7 Processor rev 1 (v7l)|Qualcomm MSM8974PRO-AC|4|Accelerometer|STMicroelectronics|Gravity|Qualcomm";
    }

    private static String getHWStatus() {
        return "mtp,adb|mtp,adb|mtp,adb|MPSS.DI.4.0-eaa9d90|Qualcomm RIL 1.0|wlan0|ABSENT||0|0|1|1|1|1|1|1|0|1|1|1";
    }

    private static String getHWProperty() {
        return "MSM8974|Xiaomi|cancro_wc_lte|cancro|MMB29M|zh|CN|Xiaomi|MI 4LTE|6.0.1|23|release-keys|Xiaomi/cancro_wc_lte/cancro:6.0.1/MMB29M/8.9.13:user/release-keys|qcom|c3-miui-ota-bd146.bj|user|cancro|armeabi-v7a|armeabi|cancro-user 6.0.1 MMB29M 8.9.13 release-keys|1|1|";
    }

    public static void main(String[] args) {
        System.out.println(startCollection());
        //wm_sign
        String str = a("/api/v6/smartassistant/showentrance", "866963024025332", 1581060659144L, 26);
        System.out.println(str);
    }
}
