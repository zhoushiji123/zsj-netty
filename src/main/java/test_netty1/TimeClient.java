package test_netty1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by zsj on 2017/6/24.
 */
public class TimeClient {

    /**
     * 连接到server
     * @param host
     * @param port
     * @throws Exception
     */
    public void connect(String host,int port) throws Exception{
        //创建NIO线程管理组
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try{
            //创建客户端启动辅助类
            Bootstrap bootstrap = new Bootstrap();

            //设定管理组和channel类型，tcp连接类型,绑定事件处理类
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,
                    true).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new TimeClientHandler());
                }
            });

            //发起异步连接操作
            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();

            //阻塞等待客户端链路关闭
            channelFuture.channel().close().sync();
        }finally {
            //退出程序,关闭nio线程管理组
            eventLoopGroup.shutdownGracefully();
        }
    }



    public static void main(String[] args) {
        int port = 8080;
        String host = "127.0.0.1";

        try {
            new TimeClient().connect(host,port);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
