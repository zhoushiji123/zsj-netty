package test_netty1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import zucc.zhoushiji.utils.DateUtil;

/**
 * Created by zsj on 2017/6/24.
 */
public class TimeServerHandler extends ChannelHandlerAdapter {


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf receiveBuf = (ByteBuf)msg;
        byte[] bytes = new byte[receiveBuf.readableBytes()];//获取缓冲区可读数据的字节数,并构造字节数组
        receiveBuf.readBytes(bytes);  //复制数据到字节数组中

        String body = new String(bytes,"UTF-8");

        System.out.println("time server receive order :"+body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)? DateUtil.getCurrentTime():"BAD ORDER";

        ByteBuf responceBuf = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.write(responceBuf); //写入数据到缓冲区中
        System.out.println("server sends message succeed : "+currentTime);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();//把数据写入SocketChannel中并发送
    }
}
