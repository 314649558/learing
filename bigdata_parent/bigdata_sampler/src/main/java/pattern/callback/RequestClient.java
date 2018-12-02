package pattern.callback;

/**
 * Created by Administrator on 2018/12/2.
 */
public class RequestClient {
    public static void main(String[] args) {

        RequestFuture future=new RequestFuture();
        future.send("hailongdongdong").addListener(new RequestListener() {
            @Override
            public void onSuccess() {
                System.out.println("消息发送成功");
            }

            @Override
            public void onFailure() {
                System.out.println("消息发送失败");
            }
        });


    }
}
