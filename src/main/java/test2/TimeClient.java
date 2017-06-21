package test2;

/**
 * Created by zsj on 2017/6/20.
 * NIO TimeClient
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        new Thread(new TimeClientHandle("127.0.0.1",port)).start();
    }
}
