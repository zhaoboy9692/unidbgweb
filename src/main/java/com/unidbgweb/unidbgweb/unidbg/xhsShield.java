package com.unidbgweb.unidbgweb.unidbg;

import cn.banny.unidbg.Emulator;
import cn.banny.unidbg.LibraryResolver;
import cn.banny.unidbg.Module;
import cn.banny.unidbg.arm.ARMEmulator;
import cn.banny.unidbg.arm.HookStatus;
import cn.banny.unidbg.file.FileIO;
import cn.banny.unidbg.file.IOResolver;
import cn.banny.unidbg.hook.ReplaceCallback;
import cn.banny.unidbg.hook.xhook.IxHook;
import cn.banny.unidbg.linux.android.AndroidARMEmulator;
import cn.banny.unidbg.linux.android.AndroidResolver;
import cn.banny.unidbg.linux.android.XHookImpl;
import cn.banny.unidbg.linux.android.dvm.*;
import cn.banny.unidbg.linux.android.dvm.array.ByteArray;
import cn.banny.unidbg.linux.file.ByteArrayFileIO;
import cn.banny.unidbg.memory.Memory;
import cn.banny.unidbg.pointer.UnicornPointer;
import com.sun.jna.Pointer;
import unicorn.ArmConst;
import unicorn.Unicorn;

import java.io.File;
import java.io.IOException;

/**
 * Created by apple on 2019/4/23.
 */
public class xhsShield extends AbstractJni implements IOResolver {

    private static LibraryResolver createLibraryResolver() {
        return new AndroidResolver(23);
    }

    private static ARMEmulator createARMEmulator() {
        return new AndroidARMEmulator("com.xingin.xhs");
    }

    private final ARMEmulator emulator;
    private final VM vm;
    private final Module module;
    private final DvmClass RedHttpInterceptor;
    //    private final String soName = "libshield.so";                     //5.44.0
//    private final String soName = "libshield_update.so";              //5.44.0
//    private final String soName = "libshield5.26.0.so";               //5.26.0
//    private final String soName = "libshield5.26.0_update.so";        //5.26.0
//    private final String soName = "libshield5.26.0_updateS1.so";        //5.26.0
    //    private final String soName = "libshield5.26.0_update2.so";        //5.26.0
    private String shield = "";
    private String params = null;
    private String sessionId = null;
    private String deviceId = null;
    private String userAgent = null;
    private String libName = "libshield526.so";
    private Boolean flag = false;

    public xhsShield() throws IOException {
        emulator = createARMEmulator();
        emulator.getSyscallHandler().addIOResolver(this);
        emulator.getMemory().setCallInitFunction();
        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(createLibraryResolver());

        vm = emulator.createDalvikVM(new File("src/main/resources/app/xhs/xhs526.apk"));
        vm.setJni(this);
//        DalvikModule dm = vm.loadLibrary("shield",false);
        DalvikModule dm = vm.loadLibrary(new File("src/main/resources/app/xhs/libshield526.so"), false);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        RedHttpInterceptor = vm.resolveClass("com/xingin/shield/http/RedHttpInterceptor");
        RedHttpInterceptor.callStaticJniMethod(emulator, "initializeNative()V");
        vm.deleteLocalRefs();
    }

    public String getShield(String params, String sessionId, String deviceId, String userAgent,String ss) {
        this.params = params;
        this.sessionId = sessionId;
        this.deviceId = deviceId;
        this.userAgent = userAgent;

        DvmClass Chain = vm.resolveClass("okhttp3/Interceptor$Chain");
//        long start = System.currentTimeMillis();
        //======libshield.so======
//        emulator.traceCode();
//        emulator.attach().addBreakPoint(null, 0x400259a8);
//        emulator.attach().addBreakPoint(null, 0x40015d62);//shield sign r0=crypt_method
//        emulator.attach().addBreakPoint(null, 0x40015508);//shield crypt_method:string2bytes:  this=crypt_method  a2=result a3= crypt_method_len a4=
//        emulator.attach().addBreakPoint(null, 0x40015d74);//r0= crypt_method:string2bytes()的结果
//        emulator.attach().addBreakPoint(null, 0x40019604);// S4方法，这里是计算shield的地方。
//        emulator.attach().addBreakPoint(null, 0x40015500);//shield crypt_method:string2bytes:  r4=result
        //========================


        //======libshield5.26.0_update.so======
//        emulator.traceCode();
//        emulator.attach().addBreakPoint(null, 0x4000F54C);//r4为需要对比的值
//        emulator.attach().addBreakPoint(null, 0x4000F4F0);//v14为 DE C9 79 4B C5 88 60 26 不等于s1 s2 s3 s4中任何一个值
//        emulator.traceRead(0x40251050, 0x40251050 + 20);
//        String aaa = "S2";
//        ByteArray bbb = new ByteArray(aaa.getBytes());
//        vm.addLocalObject(bbb);
//        emulator.traceWrite(0x40251050, vm.addLocalObject(bbb));
//        emulator.traceRead(0x40251050, 0x40251050 + 20);

        //========================
        if (!flag) {
            IxHook xHook = XHookImpl.getInstance(emulator);
            xHook.register(libName, "strcmp", new ReplaceCallback() {
                @Override
                public HookStatus onCall(Emulator emulator, long originFunction) {
                    Unicorn unicorn = emulator.getUnicorn();
                    Pointer src = UnicornPointer.register(emulator, ArmConst.UC_ARM_REG_R0);
                    //S1、S2、S3、S4 4种算法
                    src.setString(0, ss);
                    //e81d606b0e0a961e65c 9ea01c1547cf4
                    //8327588fd86071b2b4883525e878bed9
                    //8007b00cc54f8a981a4e1af1dbc48f7cf7454ed7b024e1471985449cccc2a8aa
                    //88e04c716e9fdb369c6b876670c6650845394d2f2fd142c453ee7a4f8b8dc339
//                Pointer dest = UnicornPointer.register(emulator, ArmConst.UC_ARM_REG_R1);
//                String str = dest.getString(0);
//                Inspector.inspect(src.getByteArray(0, str.length()), "strcmp dest=" + str);
                    return HookStatus.RET(unicorn, originFunction);
                }
            });
            xHook.refresh();
            this.flag = true;
        }
        Number ret = RedHttpInterceptor.newObject(null).callJniMethod(emulator, "process(Lokhttp3/Interceptor$Chain;)Lokhttp3/Response;", Chain.newObject(null));
        long hash = ret.intValue() & 0xffffffffL;
        DvmObject obj = vm.getObject(hash);
        vm.deleteLocalRefs();
//        System.out.println("process ret=0x" + Integer.toHexString(ret.intValue()) + ", obj=" + obj.getValue() + ", offset=" + (System.currentTimeMillis() - start) + "ms");
        return shield;
    }

    public void destroy() throws IOException {
        emulator.close();
    }

    @Override
    public DvmObject callObjectMethodV(BaseVM vm, DvmObject dvmObject, String signature, VaList vaList) {
//        System.out.println("call: " + signature);
        switch (signature) {
            case "com/xingin/shield/http/RedHttpInterceptor->deviceId()Ljava/lang/String;":
                return new StringObject(vm, deviceId);
            case "com/xingin/shield/http/RedHttpInterceptor->sessionId()Ljava/lang/String;":
                return new StringObject(vm, sessionId);
            case "com/xingin/shield/http/RedHttpInterceptor->userAgent()Ljava/lang/String;":
                return new StringObject(vm, userAgent);
            case "okhttp3/Interceptor$Chain->request()Lokhttp3/Request;":
                DvmClass clazz = vm.resolveClass("okhttp3/Request");
                return clazz.newObject(null);
            case "okhttp3/Request->newBuilder()Lokhttp3/Request$Builder;":
                clazz = vm.resolveClass("okhttp3/Request$Builder");
                return clazz.newObject(null);
            case "com/xingin/shield/http/RedHttpInterceptor->getBytesOfParams(Lokhttp3/Request;)[B":
                byte[] bytes = (params).getBytes();
                return new ByteArray(bytes);
            case "okhttp3/Request$Builder->header(Ljava/lang/String;Ljava/lang/String;)Lokhttp3/Request$Builder;":
                StringObject name = vaList.getObject(0);
                StringObject value = vaList.getObject(4);
                if (name.getValue().equals("shield")) {
                    shield = value.getValue();
                }
//                System.err.println("okhttp3/Request$Builder->header name=" + name.getValue() + ", value=" + value.getValue());
                return dvmObject;
            case "okhttp3/Request$Builder->build()Lokhttp3/Request;":
                clazz = vm.resolveClass("okhttp3/Request");
                return clazz.newObject(null);
            case "okhttp3/Interceptor$Chain->proceed(Lokhttp3/Request;)Lokhttp3/Response;":
                clazz = vm.resolveClass("okhttp3/Response");
                return clazz.newObject(null);
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
}
