package com.unidbgweb.unidbgweb.unidbg;

import cn.banny.unidbg.Module;
import cn.banny.unidbg.arm.ARMEmulator;
import cn.banny.unidbg.file.FileIO;
import cn.banny.unidbg.file.IOResolver;
import cn.banny.unidbg.linux.android.AndroidARMEmulator;
import cn.banny.unidbg.linux.android.AndroidResolver;
import cn.banny.unidbg.linux.android.dvm.*;
import cn.banny.unidbg.linux.android.dvm.array.ByteArray;
import cn.banny.unidbg.memory.Memory;

import java.io.File;
import java.io.IOException;

public class KuaiShouSign extends AbstractJni implements IOResolver {

    private static final String APP_PACKAGE_NAME = "com.smile.gifmaker";

    private final ARMEmulator emulator;
    private final VM vm;

    private final DvmClass cCPUJni;

    private static final String APK_PATH = "src/main/resources/app/kuaishou/kuaishou6.2.3.8614.apk";

    private final Module module;

    public KuaiShouSign() throws IOException {
        emulator = new AndroidARMEmulator(APP_PACKAGE_NAME);
        emulator.getSyscallHandler().addIOResolver(this);
        System.out.println("== init ===");

        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        memory.setCallInitFunction();

        vm = emulator.createDalvikVM(new File(APK_PATH));
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dm = vm.loadLibrary("core", false);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();

        cCPUJni = vm.resolveClass("com/yxcorp/gifshow/util/CPU");
    }

    private void destroy() throws IOException {
        emulator.close();
        System.out.println("module=" + module);
        System.out.println("== destroy ===");
    }

    public static void main(String[] args) throws Exception {
        KuaiShouSign test = new KuaiShouSign();
        String str = "app=0appver=6.11.1.11975bodyMd5=4c99047b2088073a03bb5d5cad0e4bbdbrowseType=1c=ALI_CPD,9client_key=3c2cd3f3country_code=cndid=ANDROID_836c8543741262cdegid=DFP65CB21958A7DEFD9D2B15E1EC1FF72263C4D76FDC9D0D61957F584F2453ADencoding=zstdftt=hotfix_ver=isp=CTCCiuid=kpf=ANDROID_PHONEkpn=KUAISHOUlanguage=zh-cnlat=22.67393lon=113.929135max_memory=384mod=HUAWEI(BLN-AL10)net=WIFIoc=UNKNOWNos=androidpriorityType=1sh=1920socName=: HiSilicon Kirin 650sw=1080sys=ANDROID_6.0token=efe1d029308c42da966576739e5b8374-1489600782ud=1489600782ver=6.11";
        System.out.println(test.getSign(str));
        test.destroy();
    }

    public String getSign(String params) {

//        emulator.traceCode();
//        emulator.attach().addBreakPoint(null, 0x40001278);
        DvmObject<?> context = vm.resolveClass("com/yxcorp/gifshow/App").newObject(null);
        Number ret = cCPUJni.callStaticJniMethod(emulator, "getClock(Ljava/lang/Object;[BI)Ljava/lang/String;",
                context,
                new ByteArray(params.getBytes()), 23);
        long hash = ret.intValue() & 0xffffffffL;
        StringObject obj = vm.getObject(hash);
        vm.deleteLocalRefs();
       return obj.getValue();
    }

    @Override
    public FileIO resolve(File workDir, String pathname, int oflags) {
        System.out.println("resolve pathname=" + pathname);
        return null;
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "com/yxcorp/gifshow/App->getPackageName()Ljava/lang/String;":
                return new StringObject(vm, APP_PACKAGE_NAME);
            case "com/yxcorp/gifshow/App->getPackageManager()Landroid/content/pm/PackageManager;":
                return vm.resolveClass("android/content/pm/PackageManager").newObject(null);
        }

        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

}