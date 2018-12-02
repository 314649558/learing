package pattern.callback;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2018/12/2.
 */
public class RequestFuture {

    private BlockingQueue<RequestListener> queue=new LinkedBlockingQueue<>();

    private boolean flag=false;


    public RequestFuture send(String msg){
        if(msg.contains("hailong")){
            flag=true;
        }else{
            flag=false;
        }
        return this;
    }

    public void addListener(RequestListener listener){
        queue.add(listener);
        if(flag){
            queue.poll().onSuccess();
        }else{
            queue.poll().onFailure();
        }
    }



}
