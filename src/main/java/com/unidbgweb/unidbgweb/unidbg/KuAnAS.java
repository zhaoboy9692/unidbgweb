package com.unidbgweb.unidbgweb.unidbg;

import cn.banny.unidbg.Module;
import cn.banny.unidbg.arm.ARMEmulator;
import cn.banny.unidbg.linux.android.AndroidARMEmulator;
import cn.banny.unidbg.linux.android.AndroidResolver;
import cn.banny.unidbg.linux.android.dvm.*;
import cn.banny.unidbg.memory.Memory;

import java.io.File;
import java.io.IOException;

//酷安app
public class KuAnAS extends AbstractJni {
    private static final String APP_PACKAGE_NAME = "com.coolapk.market";
    private final ARMEmulator emulator;
    private final VM vm;
    private final Module module;

    private final DvmClass TinyEncode;

    public KuAnAS() throws IOException {
        emulator = new AndroidARMEmulator(APP_PACKAGE_NAME);
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        memory.setCallInitFunction();

        vm = emulator.createDalvikVM(new File("src/main/resources/app/kuAn/Coolapk-9.6.3-1910291-coolapk-app-release.apk"));
        vm.setJni(this);
        DalvikModule dm = vm.loadLibrary("native-lib", false);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        TinyEncode = vm.resolveClass("com/coolapk/market/util/AuthUtils");
    }

    public String getAS(String deviceId) {
        DvmObject context = vm.resolveClass("android/content/ContextWrapper").newObject(null);
        Number ret = TinyEncode.callStaticJniMethod(emulator, "getAS(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;", context, vm.addLocalObject(new StringObject(vm, deviceId)));
        long hash = ret.intValue() & 0xffffffffL;
//        System.out.println("getNativeVersion version=" + hash + ", offset=" + (System.currentTimeMillis() - start) + "ms");
        StringObject version = vm.getObject(hash);
        vm.deleteLocalRefs();
        return version.getValue();
//        System.out.println("getNativeVersion version=" +version.getValue()  + ", offset=" + (System.currentTimeMillis() - start) + "ms");
    }



    @Override
    public DvmObject callObjectMethodV(BaseVM vm, DvmObject dvmObject, String signature, VaList vaList) {
        if ("android/content/pm/Signature->toCharsString()Ljava/lang/String;".equals(signature)) {
            return new StringObject(vm, "30820259308201c2a00302010202045044cd17300d06092a864886f70d01010505003071310b300906035504061302434e310f300d06035504080c06e58c97e4baac310f300d06035504070c06e58c97e4baac31143012060355040a130b436f6f6c41706b2e636f6d31143012060355040b130b436f6f6c41706b2e636f6d311430120603550403130b436f6f6c41706b2e636f6d301e170d3132303930333135333033315a170d3430303132303135333033315a3071310b300906035504061302434e310f300d06035504080c06e58c97e4baac310f300d06035504070c06e58c97e4baac31143012060355040a130b436f6f6c41706b2e636f6d31143012060355040b130b436f6f6c41706b2e636f6d311430120603550403130b436f6f6c41706b2e636f6d30819f300d06092a864886f70d010101050003818d0030818902818100b1441c2288e4de72d2c7e81a3ab29e2e63ca3ad271636dfdac60eb9c0d5b4b67ed6be9d236bc49087c1c207b4bdcd1fc6150198fbdf3f882c04c8415d953508ea117cb1eaf3f06fc7f55086dc125ad477ebd7db98fd9769934915b72aaaf1276b1fcd7b5f7f779c3b2ebc4b701781f4d00810bd57ace023c7cab757314184f2d0203010001300d06092a864886f70d01010505000381810066e7f8317544e55b4b606bb00426179d0bdee1d865920abd39bf6273e369b15a53efe96a745d0b53051805d15af7bb8d59b87d5dfc6cb1f0afeecce2d12c8c3612b9c2479188db38a8026092f71ddc1ec67c5b312ea1ff78053901bd0dcf1c2282748a657f110e7dac40575e9547c5d2383de10d618f981b419fbefddec4b240");
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

}


