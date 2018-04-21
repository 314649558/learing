package jvm.ch2;

/**
 * 虚拟机栈和本地方法栈溢出
 * VM ARGS: -Xss128k
 */
public class JavaVMStackSOF {
    private int stackLength = 1;
    public void stackLeak(){
        stackLength+=1;
        stackLeak();
    }

    public static void main(String[] args) {
        JavaVMStackSOF sof=new JavaVMStackSOF ();
        try{
            sof.stackLeak ();
        }catch (Throwable e){
            System.out.println ("stack length:"+sof.stackLength);
            throw  e;
        }
    }
}
