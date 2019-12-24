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

public class duEncodeByte {
    //ARM模拟器
    private final ARMEmulator emulator;
    //vm
    private final VM vm;
    //载入的模块
    private final Module module;

    private final DvmClass TTEncryptUtils;

    //初始化
    public duEncodeByte() throws IOException {
        //创建毒进程，这里我用了天眼查的也可以，不知道咋回事
        emulator = new AndroidARMEmulator("com.du.du");
        Memory memory = emulator.getMemory();
        //作者支持19和23两个sdk
        memory.setLibraryResolver(new AndroidResolver(23));
        memory.setCallInitFunction();
        //创建DalvikVM，利用apk本身，可以为null
        //如果用apk文件加载so的话，会自动处理签名方面的jni，具体可看AbstractJni,这就是利用apk加载的好处
//        vm = emulator.createDalvikVM(new File("src/test/resources/du/du4160.apk"));
        vm = emulator.createDalvikVM(null);
        //加载so，使用armv8-64速度会快很多
        DalvikModule dm = vm.loadLibrary(new File("src/main/resources/app/du/libJNIEncrypt.so"), false);
        //调用jni
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        //Jni调用的类，加载so
        TTEncryptUtils = vm.resolveClass("com/duapp/aesjni/AESEncrypt");
    }


    //关闭模拟器
    public void destroy() throws IOException {
        emulator.close();
        System.out.println("destroy");
    }

    public String encodeByte(String value) {
//        IxHook xHook = XHookImpl.getInstance(emulator);
//        //hook注册，源码中只有这一样覆盖方法，类似xposed的hook修改值，这里应该和xposed的afterhook一样
//        xHook.register("libJNIEncrypt.so", "AES128_ECB_encrypt", new ReplaceCallback() {
//            @Override
//            public HookStatus onCall(Emulator emulator, long originFunction) {
//                //指向内存地址
//                //getPointerArg(0); 获取第一个参数，如果获取第二个参数，则需要重新声明并改成1
//                Pointer pointer = emulator.getContext().getPointerArg(0);
//                Pointer pointer2 = emulator.getContext().getPointerArg(1);
//                //hook修改参数
////                pointer.setString(0,"23456");
//                System.out.println("one=>" + pointer.getString(0) + "two->" + pointer2.getString(0));
//                return HookStatus.RET(emulator, originFunction);
//            }
//        });
//        //这里源码会去回调，才能执行上述方法
//        xHook.refresh();
        //调试
        // 这里还支持gdb调试，
        //emulator.attach(DebuggerType.GDB_SERVER);
        //附加调试器
//        emulator.attach(DebuggerType.SIMPLE);
//        emulator.traceCode();
        //这里是打断点，原地址0x00005028->新地址0x40005028 新地址需要改成0x4
//        emulator.attach().addBreakPoint(null, 0x40001188);//encode地址
//        emulator.attach().addBreakPoint(null, 0x40000D10);
        Number ret = TTEncryptUtils.callStaticJniMethod(emulator, "getByteValues()Ljava/lang/String;");
        long hash = ret.intValue() & 0xffffffffL;
        StringObject st1 = vm.getObject(hash);
        //毒这里要处理下字符串
        String byteString = st1.getValue();
        StringBuilder builder = new StringBuilder(byteString.length());
        for (int i = 0; i < byteString.length(); i++) {
            if (byteString.charAt(i) == '0') {
                builder.append('1');
            } else {
                builder.append('0');
            }
        }
        //获取encodeByte地址
        ret = TTEncryptUtils.callStaticJniMethod(emulator, "encodeByte(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
                //传参，这里需要两个字符串，所以就传入两个参数
                vm.addLocalObject(new StringObject(vm, value)),
                vm.addLocalObject(new StringObject(vm, builder.toString())));
        //ret 返回的是地址，
        hash = ret.intValue() & 0xffffffffL;
        //或得其值
        StringObject str = vm.getObject(hash);
        System.out.println(str.getValue());
        return str.getValue();
    }
}
