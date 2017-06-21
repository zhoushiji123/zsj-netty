package test2;

/**
 * Created by zsj on 2017/6/19.
 * 基于nio的TimeServer
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        MultiplexerTimeServer timeServer =new MultiplexerTimeServer(port);
        new Thread(timeServer,"NIO-TimeServer-001").start();
    }
}
