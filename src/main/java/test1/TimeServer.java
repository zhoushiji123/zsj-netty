package test1;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zsj on 2017/6/16.
 * 基于同步阻塞式的BIO 网络socket编程
 */
public class TimeServer {
    public static void main(String[] args) throws Exception{
        int port = 8080 ;
        ServerSocket serverSocket = null;
        Socket socket;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("time server 启动,端口号: "+port);
            while (true){
                socket = serverSocket.accept();
                System.out.println("接收到一个新连接！");
                new Thread(new TimeServerHandler(socket)).start();
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
