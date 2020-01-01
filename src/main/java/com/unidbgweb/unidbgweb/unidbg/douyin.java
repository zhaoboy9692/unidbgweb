package com.unidbgweb.unidbgweb.unidbg;

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

public class douyin extends AbstractJni implements IOResolver {
    //ARM模拟器
    private final ARMEmulator emulator;
    //vm
    private final VM vm;
    //载入的模块
    private final Module module;
    private final DvmClass a;


    //初始化
    public douyin() throws IOException {
        //创建毒进程，这里我用了天眼查的也可以，不知道咋回事
        emulator = new AndroidARMEmulator("com.du.du");
        Memory memory = emulator.getMemory();
        //作者支持19和23两个sdk
        memory.setLibraryResolver(new AndroidResolver(19));
        memory.setCallInitFunction();
        //创建DalvikVM，利用apk本身，可以为null
        //如果用apk文件加载so的话，会自动处理签名方面的jni，具体可看AbstractJni,这就是利用apk加载的好处
        vm = emulator.createDalvikVM(new File("src/main/resources/app/douyin/douyin.apk"));
//        vm = emulator.createDalvikVM(null);
        vm.setJni(this);
        vm.setVerbose(true);
        //加载so，使用armv8-64速度会快很多
        DalvikModule dm = vm.loadLibrary("cms", false);
        //调用jni
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        //Jni调用的类，加载so
//        UserInfo = vm.resolveClass("com/ss/sys/ces/a");
        a = vm.resolveClass("com/ss/sys/ces/a");
//        UserInfo.callStaticJniMethod(emulator, "initUser(Ljava/lang/String;)I", vm.addLocalObject(new StringObject(vm, "2a35c29661d45a80fdf0e73ba5015be19f919081b023e952c7928006fa7a11b3")));
    }

    //关闭模拟器
    public void destroy() throws IOException {
        emulator.close();
        System.out.println("destroy");
    }

    public static void main(String[] args) throws IOException {
        douyin douyin = new douyin();
        douyin.aa();
    }

    private void aa() {
        Number ret = a.callStaticJniMethod(emulator, "leviathan(II[B)[B",
                123456,
                123456,
                vm.addLocalObject(new ByteArray("".getBytes()))
        );
        long hash = ret.intValue() & 0xffffffffL;
        ByteArray obj = vm.getObject(hash);
        System.out.println(obj);
    }

    @Override
    public DvmObject callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "java/lang/Thread->currentThread()Ljava/lang/Thread;":
                DvmClass clazz = vm.resolveClass("java/lang/Thread");
                return clazz.newObject(null);
            case "android/net/Uri->parse(Ljava/lang/String;)Landroid/net/Uri;":
                clazz = vm.resolveClass("android/net/Uri");
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
            case "android/app/Application->getApplicationInfo()Landroid/content/pm/ApplicationInfo;":
                return vm.resolveClass("android/content/pm/ApplicationInfo").newObject(null);
            case "android/content/ContentResolver->call(Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;Landroid/os/Bundle;)Landroid/os/Bundle;":
                return vm.resolveClass("android/os/Bundle;").newObject(null);
            case "android/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;":
                return vm.resolveClass("android/os/Bundle;").newObject(null);
            case "android/os/Bundle;->getBytes(Ljava/lang/String;)[B":
                s = vaList.getObject(0);
                System.out.println(s.getValue());
                return new ByteArray((s.getValue()).getBytes());
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> getObjectField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        if ("android/content/pm/ApplicationInfo->sourceDir:Ljava/lang/String;".equals(signature)) {
            return new StringObject(vm, "/data/data/com.ss.android.ugc.aweme/");
        }
        return super.getObjectField(vm, dvmObject, signature);
    }

    @Override
    public DvmObject<ByteArray> newObjectV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        if ("java/lang/String-><init>([BLjava/lang/String;)V".equals(signature)) {
            ByteArray array = vaList.getObject(0);
            StringObject str = vaList.getObject(1);
            return new DvmObject<>(vm.resolveClass("java/lang/String"), new ByteArray(array.getValue()));
        }

        throw new AbstractMethodError(signature);
    }

    @Override
    public FileIO resolve(File workDir, String pathname, int oflags) {
        if (("proc/" + emulator.getPid() + "/status").equals(pathname)) {
            return new ByteArrayFileIO(oflags, pathname, "TracerPid:\t0\n".getBytes());
        }
        return null;
    }
}
