package test1_2;

import zucc.zhoushiji.utils.DateUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by zsj on 2017/6/16.
 * TimeServer接收到连接后，就新建一个线程。
 * 每个线程是每个socket对应的服务线程。
 */
public class TimeServerHandler implements Runnable{

    private Socket socket;

    public TimeServerHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(),true);
            String currentTime = null;
            String body = null;

            while (true){
                body = bufferedReader.readLine();

                if (body == null)
                    break;
                System.out.println("The time server receive order: "+body);
                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)? DateUtil.getCurrentTime():"BAD ORDER";

                printWriter.println(currentTime);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
                printWriter.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
