package com.hailong.jvm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2019/7/18.
 *
 * -verbos:class 跟踪类的加载和卸载信息
 * -XX:+TraceClassUnloading  跟踪类的卸载
 * -XX:+TraceClassLoading    跟踪类的加载
 *
 * -XX:+PrintClassHistogram  查看系统中类的分布情况
 */
public class ASMDynamicGenClass implements Opcodes {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);

        cw.visit(V1_7,ACC_PUBLIC,"Example",null,"java/lang/Object",null);

        MethodVisitor mw=cw.visitMethod(ACC_PUBLIC,"<init>" ,"()V",null,null);

        mw.visitVarInsn(ALOAD,0);
        mw.visitMethodInsn(INVOKESPECIAL,"java/lang/Object","<init>","()V");
        mw.visitInsn(RETURN);
        mw.visitMaxs(0,0);
        mw.visitEnd();
        mw=cw.visitMethod(ACC_PUBLIC+ACC_STATIC,"main","([Ljava/lang/String;)V)",null,null);
        mw.visitFieldInsn(GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;");
        mw.visitLdcInsn("Hello world!");
        mw.visitMethodInsn(INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String)V");
        mw.visitInsn(RETURN);
        mw.visitMaxs(0,0);
        mw.visitEnd();
        byte[] code=cw.toByteArray();

        for (int i=0;i<2;i++){
            ASMDynamicGenClass loader=new ASMDynamicGenClass();

            Method m=ClassLoader.class.getDeclaredMethod("defineClass",
                    String.class,byte[].class,int.class,int.class);

            m.setAccessible(true);
            m.invoke("Example",code,0,code.length);
            m.setAccessible(false);
            System.gc();
        }
    }
}
