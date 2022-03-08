package com.heyi.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

//public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    public class EchoClientHandler  {


    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!",CharsetUtil.UTF_8));
    }

    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("Client received: "+msg.toString(CharsetUtil.UTF_8));
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();
    }
}
