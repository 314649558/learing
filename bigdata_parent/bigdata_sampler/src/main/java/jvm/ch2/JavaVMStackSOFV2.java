package jvm.ch2;

/**
 * 虚拟机栈和本地方法栈溢出
 * VM ARGS: -Xss128k
 */
public class JavaVMStackSOFV2 {


    private void dontStop(){
        while(true){

        }
    }


    public void stackLeakByThread(){
        while(true){
            Thread thread=new Thread (new Runnable () {
                @Override
                public void run() {
                    dontStop ();  //线程启动后就永不停止
                }
            });

            thread.start ();
        }
    }

    public static void main(String[] args) {
        JavaVMStackSOFV2 sof=new JavaVMStackSOFV2 ();
        sof.stackLeakByThread ();
    }


}
