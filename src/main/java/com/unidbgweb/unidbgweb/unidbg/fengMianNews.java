package com.unidbgweb.unidbgweb.unidbg;

import cn.banny.unidbg.Module;
import cn.banny.unidbg.arm.ARMEmulator;
import cn.banny.unidbg.linux.android.AndroidARMEmulator;
import cn.banny.unidbg.linux.android.AndroidResolver;
import cn.banny.unidbg.linux.android.dvm.*;
import cn.banny.unidbg.memory.Memory;

import java.io.File;
import java.io.IOException;

//封面新闻5.6

public class fengMianNews extends AbstractJni {
    //ARM模拟器
    private final ARMEmulator emulator;
    //vm
    private final VM vm;
    //载入的模块
    private final Module module;

    private final DvmClass TTEncryptUtils;

    //初始化
    public fengMianNews() throws IOException {
        //创建毒进程，这里我用了天眼查的也可以，不知道咋回事
        emulator = new AndroidARMEmulator("cn.thecover.www.covermedia");
        Memory memory = emulator.getMemory();
        //作者支持19和23两个sdk
        memory.setLibraryResolver(new AndroidResolver(23));
        memory.setCallInitFunction();
        //创建DalvikVM，利用apk本身，可以为null
        //如果用apk文件加载so的话，会自动处理签名方面的jni，具体可看AbstractJni,这就是利用apk加载的好处
        vm = emulator.createDalvikVM(new File("src/main/resources/app/fengMianNews/covermedia_5.7.1_76.apk"));
//        vm = emulator.createDalvikVM(null);
        //加载so，使用armv8-64速度会快很多
        DalvikModule dm = vm.loadLibrary("wtf", false);
        //调用jni
        vm.setVerbose(true);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        //Jni调用的类，加载so
        TTEncryptUtils = vm.resolveClass("cn.thecover.lib.common.manager.SignManager".replace(".", "/"));
    }


    //关闭模拟器
    public void destroy() throws IOException {
        emulator.close();
        System.out.println("destroy");
    }

    public static void main(String[] args) throws IOException {
        fengMianNews fengMianNews = new fengMianNews();
        fengMianNews.getSign("", "", "");
    }

    public void getSign(String str1, String str2, String str3) {
        Number ret = TTEncryptUtils.callStaticJniMethod(emulator, "getSign(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", vm.addLocalObject(new StringObject(vm, str1)), vm.addLocalObject(new StringObject(vm, str2)), vm.addLocalObject(new StringObject(vm, str3)));
        long hash = ret.intValue() & 0xffffffffL;
        StringObject st1 = vm.getObject(hash);
        String byteString = st1.getValue();
        System.out.println(byteString);
    }

    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "cn/thecover/lib/common/utils/LogShutDown->getAppSign()Ljava/lang/String;":
                return new StringObject(vm, "3A6BCA056DBA41048F26197A91C0613D");
        }
        System.out.println(signature);
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }
}
