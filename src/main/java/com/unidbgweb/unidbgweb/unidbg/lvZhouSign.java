package com.unidbgweb.unidbgweb.unidbg;

import cn.banny.unidbg.Module;
import cn.banny.unidbg.arm.ARMEmulator;
import cn.banny.unidbg.linux.android.AndroidARMEmulator;
import cn.banny.unidbg.linux.android.AndroidResolver;
import cn.banny.unidbg.linux.android.dvm.*;
import cn.banny.unidbg.memory.Memory;

import java.io.File;
import java.io.IOException;

//绿洲1.8.0 有问题
public class lvZhouSign extends AbstractJni {
    private static final String APP_PACKAGE_NAME = "com.sina.oasis";
    private final ARMEmulator emulator;
    private final VM vm;
    private final Module module;

    private final DvmClass TinyEncode;

    public lvZhouSign() throws IOException {
        emulator = new AndroidARMEmulator(APP_PACKAGE_NAME);
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        memory.setCallInitFunction();

        vm = emulator.createDalvikVM(new File("src/main/resources/app/lvzhou/lvzhou1.8.0.apk"));
        vm.setJni(this);
        DalvikModule dm = vm.loadLibrary("oasiscore", false);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        TinyEncode = vm.resolveClass("com/weibo/xvideo/NativeApi");
    }

    public String getSign(String str) {
        Number ret = TinyEncode.callStaticJniMethod(emulator, "s(Ljava/lang/String;Z)Ljava/lang/String;", vm.addLocalObject(new StringObject(vm, str)),false);
        long hash = ret.intValue() & 0xffffffffL;
        StringObject version = vm.getObject(hash);
        System.out.println(version.getValue());
        return version.getValue();
    }

    public static void main(String[] args) throws IOException {
        lvZhouSign kuAnAS = new lvZhouSign();
        kuAnAS.getSign("aid=01A2UDINMYQnY6KHoZZTYEMaPRkwYMi2q4p09g4TSjNenQgFE.&cfrom=28A1395010&cuid=0&noncestr=7zqX7tF7P92pGi27ZxktN80rFPi2tS&phone=15702980072&platform=ANDROID&timestamp=1580279844255&ua=Xiaomi-MI4LTE__oasis__1.8.2__Android__Android6.0.1&version=1.8.2&vid=1014806651492&wm=5311_90005");
    }

}


