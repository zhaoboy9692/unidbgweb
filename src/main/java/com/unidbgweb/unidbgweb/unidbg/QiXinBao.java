package com.unidbgweb.unidbgweb.unidbg;

import cn.banny.unidbg.Module;
import cn.banny.unidbg.arm.ARMEmulator;
import cn.banny.unidbg.linux.android.AndroidARMEmulator;
import cn.banny.unidbg.linux.android.AndroidResolver;
import cn.banny.unidbg.linux.android.dvm.DalvikModule;
import cn.banny.unidbg.linux.android.dvm.DvmClass;
import cn.banny.unidbg.linux.android.dvm.StringObject;
import cn.banny.unidbg.linux.android.dvm.VM;
import cn.banny.unidbg.memory.Memory;

import java.io.File;
import java.io.IOException;

public class QiXinBao {
    //ARM模拟器
    private final ARMEmulator emulator;
    //vm
    private final VM vm;
    //载入的模块
    private final Module module;

    private final DvmClass TTEncryptUtils;

    //初始化
    public QiXinBao() throws IOException {
        //创建毒进程，这里我用了天眼查的也可以，不知道咋回事
        emulator = new AndroidARMEmulator("com.du.du");
        Memory memory = emulator.getMemory();
        //作者支持19和23两个sdk
        memory.setLibraryResolver(new AndroidResolver(23));
        memory.setCallInitFunction();
        //创建DalvikVM，利用apk本身，可以为null
        //如果用apk文件加载so的话，会自动处理签名方面的jni，具体可看AbstractJni,这就是利用apk加载的好处
        vm = emulator.createDalvikVM(new File("src/main/resources/app/qixinbao/qixinbao4.9.7.apk"));
//        vm = emulator.createDalvikVM(null);
        //加载so，使用armv8-64速度会快很多
        DalvikModule dm = vm.loadLibrary("encrypt-lib", false);
        //调用jni
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        //Jni调用的类，加载so
        TTEncryptUtils = vm.resolveClass("com/MessageUtil");
    }

    //关闭模拟器
    public void destroy() throws IOException {
        emulator.close();
        System.out.println("destroy");
    }

    public static void main(String[] args) throws IOException {
        QiXinBao qiXinBao = new QiXinBao();
        qiXinBao.getKey();
        qiXinBao.destroy();
    }

    private void getKey() {
        Number ret = TTEncryptUtils.callStaticJniMethod(emulator, "getKey()V");
        long hash = ret.intValue() & 0xffffffffL;
        StringObject getKey = vm.getObject(hash);
        ret = TTEncryptUtils.callStaticJniMethod(emulator, "getDECKey()V");
        hash = ret.intValue() & 0xffffffffL;
        StringObject getDECKey = vm.getObject(hash);
        ret = TTEncryptUtils.callStaticJniMethod(emulator, "getIV()V");
        hash = ret.intValue() & 0xffffffffL;
        StringObject getIV = vm.getObject(hash);
        ret = TTEncryptUtils.callStaticJniMethod(emulator, "getShareKey()V");
        hash = ret.intValue() & 0xffffffffL;
        StringObject getShareKey = vm.getObject(hash);
        System.out.println("getShareKey->"+getShareKey.getValue());
        System.out.println("getIV->"+getIV.getValue());
        System.out.println("getKey->"+getKey.getValue());
        System.out.println("getDECKey->"+getDECKey.getValue());
    }


}
