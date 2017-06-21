package test2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zsj on 2017/6/21.
 * NIO，TimeClientHandle,客户端的线程
 */
public class TimeClientHandle implements Runnable {

    private Selector selector;

    private String host;
    private int port;
    private SocketChannel socketChannel;
    private volatile boolean stop;


    public TimeClientHandle(String host,int port ) {
        this.host = host;
        this.port = port;

        try {
            selector = Selector.open();
            socketChannel =SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while(!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                SelectionKey key =null;
                while (iterator.hasNext()){
                    key = iterator.next();
                    iterator.remove();
                    handleInput(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        if(selector != null)
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


    }


    private void handleInput(SelectionKey key)throws IOException{
        if(key.isValid()){
            //判断连接
            SocketChannel socketChannel = (SocketChannel)key.channel();
            if(key.isConnectable()){
                //连接状态
                if(socketChannel.finishConnect()){
                    //连接成功
                    socketChannel.register(selector,SelectionKey.OP_READ);
                    doWrite(socketChannel);
                }else
                    System.exit(1);
            }

            if(key.isReadable()){
                //读取数据
                ByteBuffer byteBuffer =ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(byteBuffer);
                if(readBytes>0){
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);

                    String body = new String(bytes,"UTF-8");
                    System.out.println("now is :"+body);
                    this.stop =true;
                }else if(readBytes<0){
                    key.cancel();
                    socketChannel.close();
                }

            }

        }
    }

    /**
     * 连接服务器  如果连接成功则注册read在selector上
     * 若连接失败  注册connection 接收服务端连接成功的应答
     * @throws IOException
     */
    private void doConnect() throws IOException{

        if(socketChannel.connect(new InetSocketAddress(host,port))){
            socketChannel.register(selector, SelectionKey.OP_READ);
        }else
            socketChannel.register(selector,SelectionKey.OP_CONNECT);
    }


    /**
     * 发送信息给服务端
     * @param socketChannel
     * @throws IOException
     */
    private void doWrite(SocketChannel socketChannel)throws IOException{
        byte[] bytes = "QUERY TIME ORDER".getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        socketChannel.write(byteBuffer);

        if(!byteBuffer.hasRemaining())
            System.out.println("Send order to server succeed");
    }
}
