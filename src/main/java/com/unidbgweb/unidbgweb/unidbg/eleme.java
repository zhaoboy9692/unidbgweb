package com.unidbgweb.unidbgweb.unidbg;

import cn.banny.unidbg.Module;
import cn.banny.unidbg.Symbol;
import cn.banny.unidbg.arm.ARMEmulator;
import cn.banny.unidbg.linux.android.AndroidARMEmulator;
import cn.banny.unidbg.linux.android.AndroidResolver;
import cn.banny.unidbg.linux.android.dvm.*;
import cn.banny.unidbg.memory.Memory;
import cn.banny.unidbg.memory.MemoryBlock;

import java.io.File;
import java.io.IOException;
//
public class eleme extends AbstractJni {
    private static final String APP_PACKAGE_NAME = "me.ele";
    private final ARMEmulator emulator;
    private final VM vm;
    private final Module module;

    private final DvmClass TinyEncode;

    public eleme() throws IOException {
        emulator = new AndroidARMEmulator(APP_PACKAGE_NAME);
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        memory.setCallInitFunction();

        vm = emulator.createDalvikVM(new File("src/main/resources/app/eleme/eleme811.apk"));
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dm = vm.loadLibrary("uiseris", false);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        TinyEncode = vm.resolveClass("me/ele/uis/eris/ErisEntry");
//        Symbol __system_property_get = module.findSymbolByName("__system_property_get", true);
//        MemoryBlock block = memory.malloc(0x10);
//        Number ret = __system_property_get.call(emulator, "ro.build.version.sdk", block.getPointer())[0];
//        System.out.println("sdk=" + new String(block.getPointer().getByteArray(0, ret.intValue())) + ", libc=" + memory.findModule("libc.so"));
        DvmObject context = vm.resolveClass("android/content/ContextWrapper").newObject(null);
        TinyEncode.callStaticJniMethod(emulator, "instance(Landroid/content/Context;)Lme/ele/risk/jaq/JaqEntry;", context);
        TinyEncode.callStaticJniMethod(emulator, "initializer(Ljava/lang/String;)V", vm.addLocalObject(new StringObject(vm, "4f93e640-5c6f-4980-bd3d-c1256672a64d")));
//         ErisEntry.instance(context).initializer("4f93e640-5c6f-4980-bd3d-c1256672a64d");
    }

    public String getAS(String str) {

        DvmObject context = vm.resolveClass("android/content/ContextWrapper").newObject(null);
        Number ret = TinyEncode.callStaticJniMethod(emulator, "sneer(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;", context, vm.addLocalObject(new StringObject(vm, "4f93e640-5c6f-4980-bd3d-c1256672a64d")), vm.addLocalObject(new StringObject(vm, str)));
        long hash = ret.intValue() & 0xffffffffL;
//        System.out.println("getNativeVersion version=" + hash + ", offset=" + (System.currentTimeMillis()) + "ms");
        StringObject version = vm.getObject(hash);
        vm.deleteLocalRefs();
        System.out.println(version.getValue());
        return version.getValue();
//        System.out.println("getNativeVersion version=" +version.getValue()  + ", offset=" + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "java/lang/System->getProperty(Ljava/lang/String;)Ljava/lang/String;":
                StringObject string = vaList.getObject(0);
                return new StringObject(vm, System.getProperty(string.getValue()));
            case "android/os/SystemProperties->get(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;":
                string = vaList.getObject(0);
                if(string.getValue().equals("ro.build.id")){
                    return new StringObject(vm, "KTU84P");
                }else  if (string.getValue().equals("ro.mediatek.platform")){
                    return new StringObject(vm, "MT6572");
                }
                System.out.println(string+"->123456");
                return new StringObject(vm, "861385555");
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public int getStaticIntField(BaseVM vm, DvmClass dvmClass, String signature) {
        if ("android/os/Build$VERSION->SDK_INT:I".equals(signature)) {
            return 23;
        }
        return super.getStaticIntField(vm, dvmClass, signature);
    }

    public static void main(String[] args) throws IOException {
        eleme eleme = new eleme();
        eleme.getAS("");
    }

}
