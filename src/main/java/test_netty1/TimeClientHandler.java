package test_netty1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zsj on 2017/6/24.
 */
public class TimeClientHandler extends ChannelHandlerAdapter{


    private ByteBuf firstMessage ;

    public TimeClientHandler(){
        byte[] bytes = "QUERY TIME ORDER".getBytes();
        firstMessage = Unpooled.buffer(bytes.length);
        firstMessage.writeBytes(bytes);
        System.out.println("client sends message succeed");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * tcp链路建立成功之后调用。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client connect succeed");
        ctx.writeAndFlush(firstMessage);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //读取应答消息
        ByteBuf readBuf = (ByteBuf)msg;
        byte[] bytes = new byte[readBuf.readableBytes()];
        readBuf.readBytes(bytes);

        String body = new String(bytes,"UTF-8");
        System.out.println("NOW is "+body);
    }
}
