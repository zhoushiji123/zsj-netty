package test1_2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zsj on 2017/6/16.
 * 基于伪异步IO的 网络socket编程
 */
public class TimeServer {
    public static void main(String[] args) throws Exception{
        int port = 8080 ;
        ServerSocket serverSocket = null;
        Socket socket;
        TimeServerHandlerExecutePool executePool  = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("time server 启动,端口号: "+port);

            executePool = new TimeServerHandlerExecutePool(50,10000); //自定义大小的线程池

            while (true){
                socket = serverSocket.accept();
                System.out.println("接收到一个新连接！");
                executePool.execute(new TimeServerHandler(socket)); //使用线程池启动线程
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (serverSocket!= null){
                System.out.println("time server 关闭");
                serverSocket.close();
            }
        }
    }
}
