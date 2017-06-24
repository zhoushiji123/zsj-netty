package test_netty1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by zsj on 2017/6/24.
 */
public class TimeServer {

    private void  bind (int port) throws Exception{
        //配置2个NIO线程组，分别用于接收连接和socketChannel读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //netty用于启动NIO服务端的辅助启动类
        ServerBootstrap serverBootstrap =new ServerBootstrap();

        //传递参数EventLoopGroup,设置channel类型为NioServerSocketChannel,配置tcp参数，最后绑定事件处理类handler
        serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).
                option(ChannelOption.SO_BACKLOG,1024).childHandler(new ChildChannelHandler());

        //绑定端口，通过阻塞等待操作完成，完成后返回一个ChannelFuture，用于异步操作的通知回调
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

        //进行阻塞，服务端链路关闭之后main函数才结束。
        channelFuture.channel().closeFuture().sync();

        //关闭
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        try {
            new TimeServer().bind(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
