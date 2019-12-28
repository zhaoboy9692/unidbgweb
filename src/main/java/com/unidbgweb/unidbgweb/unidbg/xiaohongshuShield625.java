package com.unidbgweb.unidbgweb.unidbg;

import cn.banny.unidbg.LibraryResolver;
import cn.banny.unidbg.Module;
import cn.banny.unidbg.arm.ARMEmulator;
import cn.banny.unidbg.file.FileIO;
import cn.banny.unidbg.file.IOResolver;
import cn.banny.unidbg.linux.android.AndroidARMEmulator;
import cn.banny.unidbg.linux.android.AndroidResolver;
import cn.banny.unidbg.linux.android.dvm.*;
import cn.banny.unidbg.linux.android.dvm.array.ByteArray;
import cn.banny.unidbg.linux.file.ByteArrayFileIO;
import cn.banny.unidbg.memory.Memory;

import java.io.File;
import java.io.IOException;

public class xiaohongshuShield625 extends AbstractJni implements IOResolver {

    private static LibraryResolver createLibraryResolver() {
        return new AndroidResolver(23);
    }

    private static ARMEmulator createARMEmulator() {
        return new AndroidARMEmulator("com.xingin.xhs");
    }

    private final ARMEmulator emulator;
    private final VM vm;
    private final Module module;
    private final DvmClass XhsHttpInterceptor;
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
    private String libName = "libshield625.so";
    private Boolean flag = false;

    public xiaohongshuShield625() throws IOException {
        emulator = createARMEmulator();
        emulator.getSyscallHandler().addIOResolver(this);
        emulator.getMemory().setCallInitFunction();
        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(createLibraryResolver());

        vm = emulator.createDalvikVM(new File("src/main/resources/app/xhs/xhs625.apk"));
        vm.setJni(this);
        vm.setVerbose(true);
//        DalvikModule dm = vm.loadLibrary("shield",false);
        DalvikModule dm = vm.loadLibrary("shield", false);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        XhsHttpInterceptor = vm.resolveClass("com/xingin/shield/http/XhsHttpInterceptor");
        XhsHttpInterceptor.callStaticJniMethod(emulator, "initializeNative()V");
        vm.deleteLocalRefs();
    }

    public static void main(String[] args) throws IOException {
        xiaohongshuShield625 xh = new xiaohongshuShield625();
        xh.getShield("", "", "", "", "");
    }

    public String getShield(String params, String sessionId, String deviceId, String userAgent, String ss) {
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
//        if (!flag) {
//            IxHook xHook = XHookImpl.getInstance(emulator);
//            xHook.register(libName, "strcmp", new ReplaceCallback() {
//                @Override
//                public HookStatus onCall(Emulator emulator, long originFunction) {
//                    Unicorn unicorn = emulator.getUnicorn();
//                    Pointer src = UnicornPointer.register(emulator, ArmConst.UC_ARM_REG_R0);
//                    //S1、S2、S3、S4 4种算法
//                    src.setString(0, ss);
//                    //e81d606b0e0a961e65c 9ea01c1547cf4
//                    //8327588fd86071b2b4883525e878bed9
//                    //8007b00cc54f8a981a4e1af1dbc48f7cf7454ed7b024e1471985449cccc2a8aa
//                    //88e04c716e9fdb369c6b876670c6650845394d2f2fd142c453ee7a4f8b8dc339
////                Pointer dest = UnicornPointer.register(emulator, ArmConst.UC_ARM_REG_R1);
////                String str = dest.getString(0);
////                Inspector.inspect(src.getByteArray(0, str.length()), "strcmp dest=" + str);
//                    return HookStatus.RET(unicorn, originFunction);
//                }
//            });
//            xHook.refresh();
//            this.flag = true;
//        }
        Number ret = XhsHttpInterceptor.newObject(null).callJniMethod(emulator, "intercept(Lokhttp3/Interceptor$Chain;J)Lokhttp3/Response;", Chain.newObject(null));
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
        System.out.println("call: " + signature);
        switch (signature) {
            case "okhttp3/Interceptor$Chain->request()Lokhttp3/Request;":
                //ok
                DvmClass clazz = vm.resolveClass("okhttp3/Request");
                return clazz.newObject(null);
            case "okhttp3/Request->url()Lokhttp3/HttpUrl;":
                //ok
                clazz = vm.resolveClass("okhttp3/HttpUrl");
                return clazz.newObject(null);
            case "okhttp3/HttpUrl->encodedPath()Ljava/lang/String;":
                //ok
                String url = "https://square.github.io/okhttp/4.x/okhttp/okhttp3/-http-url/";
                return new StringObject(vm, url);
            case "okhttp3/HttpUrl->encodedQuery()Ljava/lang/String;":
                //ok**
                String Query = "channel=YingYongBaodeviceId=ec258cae-33c4-35ca-a909-67bf45c0f73edevice_fingerprint=20191123001415aae06280fc655b5c963e9db249e364ce0160af67ab7f3c61device_fingerprint1=20191123001415aae06280fc655b5c963e9db249e364ce0160af67ab7f3c61filters=[]keyword=做菜1lang=zh-Hanspage=1page_size=20platform=Androidsearch_id=EED2BE2760DAA985FCA3CE9DA636172Asid=session.1569218578701927068721sign=de46015c85b29d8cff44c3fd1535b28asort=source=search_result_notest=1575460839url=/api/sns/v8/search/notesversionName=5.26.0";
                return new StringObject(vm, Query);
            case "okhttp3/Request->body()Lokhttp3/RequestBody;":
                //ok**
                clazz = vm.resolveClass("okhttp3/RequestBody");
                return clazz.newObject(null);
            case "okhttp3/Request->headers()Lokhttp3/Headers;":
                //ok**
                StringObject array = vaList.getObject(0);
                System.out.println("*-*-"+array);
                clazz = vm.resolveClass("okhttp3/Headers");
                return clazz.newObject(null);
            case "okio/Buffer->writeString(Ljava/lang/String;Ljava/nio/charset/Charset;)Lokio/Buffer;":
                //ok**
                clazz = vm.resolveClass("okio/Buffer");
                return clazz.newObject(null);
            case "okhttp3/Headers->name(I)Ljava/lang/String;":
                //ok**
                return new StringObject(vm, "shield");
            case "okio/Buffer->readByteArray()[B":
                //ok**
                DvmObject string = dvmObject;
                StringObject encoding = vaList.getObject(0);
                System.err.println("string=" + string.getValue() + ", encoding=" + encoding.getValue());
                return new ByteArray((byte[]) string.getValue());

        }

        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public int callIntMethodV(BaseVM vm, DvmObject dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "okhttp3/Response->code()I":
                return 200;
            case "okhttp3/Headers->size()I":
                return 1;
        }
        return super.callIntMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public void callVoidMethodV(BaseVM vm, DvmObject dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "okhttp3/RequestBody->writeTo(Lokio/BufferedSink;)V":
        }
    }

    @Override
    public FileIO resolve(File workDir, String pathname, int oflags) {
        if (("proc/" + emulator.getPid() + "/status").equals(pathname)) {
            return new ByteArrayFileIO(oflags, pathname, "TracerPid:\t0\n".getBytes());
        }
        return null;
    }

    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "java/nio/charset/Charset->defaultCharset()Ljava/nio/charset/Charset;":
                DvmClass Charset = vm.resolveClass("java/nio/charset/Charset");
                return Charset.newObject(null);

        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public int getIntField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature) {
            case "android/content/pm/PackageInfo->versionCode:I":
                //build=
                return 6;
        }
        return super.getIntField(vm, dvmObject, signature);
    }

    @Override
    public DvmObject<?> getStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature) {
            case "com/xingin/shield/http/ContextHolder->deviceId:Ljava/lang/String;":
                //设备id
                return new StringObject(vm, "23456");
        }
        return super.getStaticObjectField(vm, dvmClass, signature);
    }

    @Override
    public DvmObject<?> newObjectV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        if ("okio/Buffer-><init>()V".equals(signature)) {
            return dvmClass.newObject(null);
        }

        return super.newObjectV(vm, dvmClass, signature, vaList);

    }
}
