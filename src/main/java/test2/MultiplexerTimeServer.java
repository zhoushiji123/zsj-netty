package test2;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import zucc.zhoushiji.utils.DateUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zsj on 2017/6/19.
 * 基于nio的TimeServer服务端线程
 */
public class MultiplexerTimeServer implements Runnable {

    private Selector selector;
    private ServerSocketChannel socketChannel;

    private volatile boolean stop ;

    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();
            socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);  //设置为非阻塞
            socketChannel.socket().bind(new InetSocketAddress(port),1024);//绑定端口号
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);  //注册到selector上，注册动作为接收连接
            System.out.println("The time server us start in port :"+port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public void stop(){
        this.stop =true;
    }

    @Override
    public void run() {
        while (!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();

                SelectionKey selectionKey = null;

                while(iterator.hasNext()){
                    selectionKey =  iterator.next();
                    iterator.remove();
                    try {
                        handleInput(selectionKey);
                    } catch (IOException e) {
                        if(selectionKey!=null) {
                            selectionKey.cancel();
                            if(selectionKey.channel()!=null)
                                selectionKey.channel().close();
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }


        if(selector!=null){
            try {
                selector.close(); //关闭多路复用器。
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void handleInput(SelectionKey key ) throws IOException{
        if(key.isValid()){
            //处理新接入的请求消息
            if(key.isAcceptable()){
                //接收新连接
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false); //设置客户连接为非阻塞
                socketChannel.register(selector,SelectionKey.OP_READ); //轮训read的key
                System.out.println("time server receive a connection");
            }

            if(key.isReadable()){
                //读取数据
                SocketChannel socketChannel = (SocketChannel)key.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(byteBuffer);

                if(readBytes > 0){
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    String body = new String(bytes,"UTF-8");
                    System.out.println("The time server receive order :"+body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)? DateUtil.getCurrentTime()
                            :"BAD ORDER";
                    this.doWrite(socketChannel,currentTime);

                }else  if(readBytes < 0){
                    key.cancel();
                    socketChannel.close();
                }
            }
        }
    }

    private void doWrite(SocketChannel socketChannel,String responce){
        //写内容返回给客户端
        if(responce!=null && responce.trim().length()>0){
            try {
                byte[] bytes = responce.getBytes();
                ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
                byteBuffer.put(bytes);
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
