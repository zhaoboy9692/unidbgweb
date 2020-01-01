package com.unidbgweb.unidbgweb.unidbg;

import cn.banny.unidbg.LibraryResolver;
import cn.banny.unidbg.Module;
import cn.banny.unidbg.arm.ARMEmulator;
import cn.banny.unidbg.file.FileIO;
import cn.banny.unidbg.file.IOResolver;
import cn.banny.unidbg.linux.android.AndroidARMEmulator;
import cn.banny.unidbg.linux.android.AndroidResolver;
import cn.banny.unidbg.linux.android.dvm.*;
import cn.banny.unidbg.linux.android.dvm.array.ArrayObject;
import cn.banny.unidbg.linux.android.dvm.array.ByteArray;
import cn.banny.unidbg.linux.file.ByteArrayFileIO;
import cn.banny.unidbg.memory.Memory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 今日头条
 * 有3个参数as（app每次重启会变，我模拟的不会变) mas(根据as算。这里同样as情况下算的是对的)XGorgon(根据ts和请求参数，cookie算。这里ts及请求参数一致情况下。XGorgon算的也不对)
 */
public class TouTiao extends AbstractJni implements IOResolver {

    private static LibraryResolver createLibraryResolver() {
        return new AndroidResolver(23);
    }

    private static ARMEmulator createARMEmulator() {
        return new AndroidARMEmulator("com.ss.android.article.news");
    }

    private final ARMEmulator emulator;
    private final VM vm;
    private final Module module;
    private final DvmClass UserInfo;
    private final DvmClass a;

    private String soName = "toutiao_7.2.0_libcms.so";               //5.26.0

    private String shield = "";
    private String params = null;
    private String sessionId = null;
    private String deviceId = null;
    private String userAgent = null;


    public TouTiao(String apkPath, String soPath) throws IOException {
        emulator = createARMEmulator();
        emulator.getSyscallHandler().addIOResolver(this);
        emulator.getMemory().setCallInitFunction();
        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(createLibraryResolver());

        vm = emulator.createDalvikVM(new File("src/main/resources/app/toutiao/头条720.apk"));
        vm.setJni(this);
        vm.setVerbose(true);

//        DalvikModule dm = vm.loadLibrary(new File(soPath + soName), false);
        DalvikModule dm = vm.loadLibrary("cms", false);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        UserInfo = vm.resolveClass("com/ss/android/common/applog/UserInfo");
        a = vm.resolveClass("com/ss/sys/ces/a");
        UserInfo.callStaticJniMethod(emulator, "initUser(Ljava/lang/String;)I", vm.addLocalObject(new StringObject(vm, "2a35c29661d45a80fdf0e73ba5015be19f919081b023e952c7928006fa7a11b3")));
        vm.deleteLocalRefs();
    }

    public String getAs() {
        int ts = new Long(System.currentTimeMillis() / 1000).intValue();
        String[] v0 = new String[]{"iid", "78346785189", "device_id", "68358075293", "ac", "wifi", "channel", "tengxun2", "aid", "13", "app_name", "news_article", "version_code", "720", "version_name", "7.2.0", "device_platform", "android", "ab_version", "830855,814658,662176,665174,674048,643890,1002927,649426,677129,710077,801968,707372,661907,668775,739392,662099,668774,765197,976875,857803,952275,757280,679101,660830,759654,661781", "ab_group", "100170", "ab_feature", "94563,102749", "ssmix", "a", "device_type", "Nexus 5", "device_brand", "google", "language", "zh", "os_api", "19", "os_version", "4.4.4", "uuid", "358239051535377", "openudid", "cd9a3099454bec11", "manifest_version_code", "720", "resolution", "1080*1776", "dpi", "480", "update_version_code", "72010", "rom_version", "19", "plugin", "10510", "pos", "5r_88Pzt0fzp9Ono-fi_p66ks6Srr6itpLG__PD87d706eS_p794EQp5JzF4JR-_sb_88Pzt0fLz-vTp6Pn4v6esrKuzqaiqrKur4A==", "fp", "4rTZF2qrFWFWFlTqLlU1F2USPrZ1"};
        String v2 = "https://ichannel.snssdk.com/feedback/2/list/?appkey=article-news-android&count=50&iid=78346785189&device_id=68358075293&ac=wifi&channel=tengxun2&aid=13&app_name=news_article&version_code=720&version_name=7.2.0&device_platform=android&ab_version=830855%2C814658%2C662176%2C665174%2C674048%2C643890%2C1002927%2C649426%2C677129%2C710077%2C801968%2C707372%2C661907%2C668775%2C739392%2C662099%2C668774%2C765197%2C976875%2C857803%2C952275%2C757280%2C679101%2C660830%2C759654%2C661781&ab_group=100170&ab_feature=94563%2C102749&ssmix=a&device_type=Nexus+5&device_brand=google&language=zh&os_api=19&os_version=4.4.4&uuid=358239051535377&openudid=cd9a3099454bec11&manifest_version_code=720&resolution=1080*1776&dpi=480&update_version_code=72010&_rticket=1562914688116&rom_version=19&plugin=10510&pos=5r_88Pzt0fzp9Ono-fi_p66ks6Srr6itpLG__PD87d706eS_p794EQp5JzF4JR-_sb_88Pzt0fLz-vTp6Pn4v6esrKuzqaiqrKur4A%3D%3D&fp=4rTZF2qrFWFWFlTqLlU1F2USPrZ1" + "&ts=" + ts;
        String deviceId = "68358075293";

        StringObject[] tmp = new StringObject[v0.length];
        for (int i = 0; i < v0.length; i++) {
            tmp[i] = new StringObject(vm, v0[i]);
        }
        Number ret = UserInfo.callStaticJniMethod(emulator, "getUserInfo(ILjava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
                ts,
                vm.addLocalObject(new StringObject(vm, v2)),
                vm.addLocalObject(new ArrayObject(tmp)),
                vm.addLocalObject(new StringObject(vm, deviceId))
        );
        long hash = ret.intValue() & 0xffffffffL;
        DvmObject obj = vm.getObject(hash);
        return obj.getValue().toString();
    }

    public String getMas(String as) {
        byte[] params = as.substring(0, 22).getBytes();
        Number ret = a.callStaticJniMethod(emulator, "e([B)[B",
                vm.addLocalObject(new ByteArray(params))
        );
        long hash = ret.intValue() & 0xffffffffL;
        ByteArray obj = vm.getObject(hash);
        return bytesToHexString(obj.getValue());
    }

    public String getXGorgon(int ts, String params, String cookies) {
        String m = getMD5(params) + "00000000000000000000000000000000" + getMD5(cookies) + "00000000000000000000000000000000";
        System.out.println(m);
//        m = "770eb0867318275255f2177ade6c3b17000000000000000000000000000000006fc7a149a5ba6f5fcd18ede84bb3152700000000000000000000000000000000";
//        m = "0f81f7ce916e62b0c821a8af14c092a6000000000000000000000000000000003071c3c107d67b4fabe78a275e664f7900000000000000000000000000000000";
        m = "0F81F7CE916E62B0C821A8AF14C092A6000000000000000000000000000000003071C3C107D67B4FABE78A275E664F7900000000000000000000000000000000";
        System.out.println(m);
        System.out.println("ts: " + ts);
        Number ret = a.callStaticJniMethod(emulator, "leviathan(I[B)[B",
                ts,
                vm.addLocalObject(new ByteArray(hexStringToByte(m)))
//                vm.addLocalObject(new ByteArray(m.getBytes()))
        );
        long hash = ret.intValue() & 0xffffffffL;
        ByteArray obj = vm.getObject(hash);
        return bytesToHexString(obj.getValue());
    }

    public void destroy() throws IOException {
        emulator.close();
    }


    @Override
    public DvmObject callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "java/lang/Thread->currentThread()Ljava/lang/Thread;":
                DvmClass clazz = vm.resolveClass("java/lang/Thread");
                return clazz.newObject(null);
        }


        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public DvmObject callObjectMethodV(BaseVM vm, DvmObject dvmObject, String signature, VaList vaList) {
//        System.out.println("call: " + signature);
        switch (signature) {
//            new java.lang.StackTraceElement();
            case "java/lang/String->getBytes(Ljava/lang/String;)[B":
                StringObject s = vaList.getObject(0);
//                System.out.println(s.getValue());
                byte[] bytes = (s.getValue()).getBytes();
                return new ByteArray(bytes);
            case "java/lang/Thread->getStackTrace()[Ljava/lang/StackTraceElement;":
                StackTraceElement[] elements = Thread.currentThread().getStackTrace();
                DvmObject[] objs = new DvmObject[elements.length];
                for (int i = 0; i < elements.length; i++) {
                    objs[i] = vm.resolveClass("java/lang/StackTraceElement").newObject(elements[i]);
                }
                return new ArrayObject(objs);
            case "java/lang/StackTraceElement->getClassName()Ljava/lang/String;":
                StackTraceElement element = (StackTraceElement) dvmObject.getValue();
                return new StringObject(vm, element.getClassName());
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public int callIntMethodV(BaseVM vm, DvmObject dvmObject, String signature, VaList vaList) {
        if ("okhttp3/Response->code()I".equals(signature)) {
            return 200;
        }
        return super.callIntMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public FileIO resolve(File workDir, String pathname, int oflags) {
        if (("proc/" + emulator.getPid() + "/status").equals(pathname)) {
            return new ByteArrayFileIO(oflags, pathname, "TracerPid:\t0\n".getBytes());
        }
        return null;
    }

    public static String bytesToHexString(byte[] arg9) {
        StringBuilder v1 = new StringBuilder(arg9.length * 2);
        int v4 = arg9.length;
        int v2;
        for (v2 = 0; v2 < v4; ++v2) {
            v1.append(String.format("%02x", new Integer(arg9[v2] & 0xFF)));
        }
        return v1.toString();
    }

    /**
     * 对字符串md5加密
     *
     * @param str
     * @return
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            System.out.println("md5 error");
        }
        return "";
    }

    public static byte[] hexStringToByte(String arg7) {
        int v2 = arg7.length() / 2;
        byte[] v4 = new byte[v2];
        char[] v0 = arg7.toCharArray();
        int v1;
        for (v1 = 0; v1 < v2; ++v1) {
            int v3 = v1 * 2;
            v4[v1] = ((byte) (toByte(v0[v3]) << 4 | toByte(v0[v3 + 1])));
        }

        return v4;
    }

    private static byte toByte(char arg3) {
        return ((byte) "0123456789ABCDEF".indexOf(Character.toUpperCase(arg3)));
    }


    public static void main(String[] args) throws IOException {
        TouTiao touTiao = new TouTiao("src/res/apk/toutiao_7.2.0.apk", "src/res/so/");
        String as = touTiao.getAs();
//        as = "a2755030231b8eca820144";
        String mas = touTiao.getMas(as);
        System.out.println("as: " + as);
        System.out.println("mas: " + mas);

        String params = "iid=95180342944&device_id=69864429487&ac=wifi&channel=wandoujia2&aid=13&app_name=news_article&version_code=720&version_name=7.2.0&device_platform=android&ab_version=801968%2C707372%2C668775%2C1247776%2C1387294%2C1349643%2C1370513%2C1375658%2C1190522%2C1387141%2C668779%2C1251921%2C662099%2C1353773%2C1388826%2C668774%2C1388893%2C765191%2C857804%2C660830%2C1054755%2C1230782%2C1362836%2C1243993%2C662176%2C759652&ab_group=94566%2C102754%2C181428&ab_feature=102754%2C94566&ssmix=a&device_type=Nexus+5&device_brand=google&language=zh&os_api=23&os_version=6.0.1&uuid=359250053984314&openudid=6215d092be66f146&manifest_version_code=720&resolution=1080*1776&dpi=480&update_version_code=72010&_rticket=1577277642588&plugin=26958&tma_jssdk_version=1.14.0.0&pos=5r_88Pzt3vTp5L-nv3gRCnknMXglH7-xv_zw_O3R8vP69Ono-fi_p6ysq7OpqKukrayxv_zw_O3R_On06ej5-L-nrqSzpKuvrqng&rom_version=23&ts=1577277642&as=a20545c09a3c3e78c30933&mas=004b3354095abd85b1f942373093ad568bda0fa8c8540d089f";
        String cookies = "odin_tt=a56f8477b6ffe84d3aff54d0fe44dc33d506601c6699f1c1be75a48c13fb03fa4a12fe6ecb780484420521beb641c9e5c3ecf57acc1a6356d5acaa480a115d3e; qh[360]=1; install_id=95180342944; ttreq=1$1c39a951e56332b9e594a8efca6d87be482b8518; history=alrvlFic6pJZXJCTWBmSmZt6KY7ZsHKpZ0lqbvE9BoHLsWvVlRwYmHgv8QkwOFyOY2Y5keTAxHuJ8RCIx3RbYQcDE98l%2Fktg3jeFRQ5MfJdEJUA8Bt8nXA1MPJdY3cBye8PVGZhYLknMYQi4HMfY8sH5ABP7Ja4esJk2u3tAZordAvMkbK81MDFfYudgcLgce6ftsR7ITAYGsJlmAvtB%2BgSngFWyqr8B2c7jB1L566FdAMgUjlNglczLOByYuC9JxTEEXI79dbp7JwMTzyXhMJDK37uzdjYwcVxibmFwAAAAAP%2F%2F";

//        int ts = 1577277642;
//        int ts = 1577280743;
        int ts = 1577284206;
        String XGorgon = touTiao.getXGorgon(ts, params, cookies);
        System.out.println("XGorgon: " + XGorgon);
        System.out.println("right xgorgon: 03637220800082b4584049366842c60364df4678f6d83c82a043");
    }
}
