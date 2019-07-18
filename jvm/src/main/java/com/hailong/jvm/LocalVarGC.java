package com.hailong.jvm;

/**
 * Created by Administrator on 2019/7/18.
 *
 */
public class LocalVarGC {
    public void localVarGC1(){
        byte[] a=new byte[6*1024*1024];
        System.gc();
    }

    public void localVarGC2(){
        byte[] a=new byte[6*1024*1024];
        a=null;
        System.gc();
    }

    public void localVarGC3(){
        {
            byte[] a = new byte[6 * 1024 * 1024];
        }
        System.gc();
    }

    public void localVarGC4(){
        {
            byte[] a = new byte[6 * 1024 * 1024];
        }
        int c=10;   //复制后 这个变量会使用变量a的槽位从而导致a的内存被释放掉
        System.gc();
    }

    public void localVarGC5(){
        localVarGC1();
    }


    public static void main(String[] args) {

        LocalVarGC localVarGC=new LocalVarGC();

        int flag=1;
        if(args.length>0){
            flag=Integer.valueOf(args[0]);
        }

        switch (flag){
            case 1:
                localVarGC.localVarGC1();
                break;
            case 2:
                localVarGC.localVarGC2();
                break;
            case 3:
                localVarGC.localVarGC3();
                break;
            case 4:
                localVarGC.localVarGC4();
                break;
            case 5:
                localVarGC.localVarGC5();
                break;
            default:
                System.out.println("");
        }

    }
}
