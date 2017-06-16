package test1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by zsj on 2017/6/16.
 */
public class TimeClient {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        Socket socket = null ;
        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;
        socket = new Socket("127.0.0.1",port);
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(),true);
            printWriter.println("QUERY TIME ORDER");
            System.out.println("send order to server succeed");
            String res = bufferedReader.readLine();
            System.out.println("NOW is :"+res);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            bufferedReader.close();
            printWriter.close();
            socket.close();
        }
    }
}
