package com.unidbgweb.unidbgweb.unidbg;


import cn.banny.unidbg.LibraryResolver;
import cn.banny.unidbg.Module;
import cn.banny.unidbg.arm.ARMEmulator;
import cn.banny.unidbg.file.FileIO;
import cn.banny.unidbg.file.IOResolver;
import cn.banny.unidbg.linux.android.AndroidARMEmulator;
import cn.banny.unidbg.linux.android.AndroidResolver;
import cn.banny.unidbg.linux.android.dvm.*;
import cn.banny.unidbg.memory.Memory;

import java.io.File;
import java.io.IOException;

//马蜂窝
public class AuthorizeHelper extends AbstractJni implements IOResolver {

    private static final String APP_PACKAGE_NAME = "com.mfw.roadbook";

    private static LibraryResolver createLibraryResolver() {
        return new AndroidResolver(23);
    }

    private static ARMEmulator createARMEmulator() {
        return new AndroidARMEmulator(APP_PACKAGE_NAME);
    }

    private final ARMEmulator emulator;
    private final VM vm;

    private final DvmClass AuthorizeHelper;

    private static final String APK_PATH = "src/main/resources/app/mfw/mafengwo_ziyouxing.apk";

    private final Module module;

    public AuthorizeHelper() throws IOException {
        emulator = createARMEmulator();
        emulator.getSyscallHandler().addIOResolver(this);
//        System.out.println("== init ===");

        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(createLibraryResolver());
        memory.setCallInitFunction();

        vm = emulator.createDalvikVM(new File(APK_PATH));
        vm.setJni(this);
        DalvikModule dm = vm.loadLibrary("mfw", false);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();

        AuthorizeHelper = vm.resolveClass("com/mfw/tnative/AuthorizeHelper");
    }

    private void destroy() throws IOException {
        emulator.close();
        System.out.println("module=" + module);
        System.out.println("== destroy ===");
    }

    public String getxPreAuthencode(String value) {
        DvmObject context = vm.resolveClass("android/content/Context").newObject(null);
        Number ret = AuthorizeHelper.newObject(null).callJniMethod(emulator, "xPreAuthencode(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
                context, vm.addLocalObject(new StringObject(vm, value)), vm.addLocalObject(new StringObject(vm, APP_PACKAGE_NAME)));
        long hash = ret.intValue() & 0xffffffffL;
        StringObject obj = vm.getObject(hash);
        vm.deleteLocalRefs();
        System.out.println(obj.getValue());
        return obj.getValue();
    }

    @Override
    public FileIO resolve(File workDir, String pathname, int oflags) {
        return null;
    }
}