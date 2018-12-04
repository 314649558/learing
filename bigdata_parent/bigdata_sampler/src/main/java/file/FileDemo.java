package file;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created by Administrator on 2018/12/3.
 */
public class FileDemo {
    private static String prefix="菲菲橙橙";
    private static long index=0;
    public static void main(String[] args) {
        FileDemo.fileRename("C:\\Users\\Administrator\\Desktop\\2");
    }

    public static void fileRename(String path){


        if(StringUtils.isEmpty(path)){
            return;
        }
        File file=new File(path);
        if(!file.exists()){
            return;
        }
        if(file.isDirectory()){
            File[] files=file.listFiles();
            if(files==null || files.length==0){
                return;
            }
            for(File tmpFile:files){
                if(tmpFile.isDirectory()){
                    fileRename(tmpFile.getPath());
                }else{
                    if(tmpFile.getName().endsWith("zip")){
                        continue;
                    }
                    index++;
                    String fileName= prefix+index+tmpFile.getName().substring(tmpFile.getName().length()-4,tmpFile.getName().length());
                    tmpFile.renameTo(new File(tmpFile.getPath().replace(tmpFile.getName(),fileName)));
                }
            }
        }else{
            if(file.getName().endsWith("zip")){
               return;
            }
            index++;
            String fileName= prefix+index+file.getName().substring(file.getName().length()-4,file.getName().length());
            file.renameTo(new File(file.getPath().replace(file.getName(),fileName)));
        }
       // System.out.println("总修改照片数据:"+index);
    }
}
