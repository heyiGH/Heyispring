package com.heyi.netty.myEcho;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class EchoClient {


    public void bind(int port) throws InterruptedException {

        EventLoopGroup loopGroup = new NioEventLoopGroup();

        try{
            Bootstrap b = new Bootstrap();

            b.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("127.0.0.1",port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            loopGroup.shutdownGracefully().sync();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new EchoClient().bind(8090);
    }

}

class ClientHandler extends SimpleChannelInboundHandler<ByteBuf>{

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        System.out.println("Message by server : " + byteBuf.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Client request: hello!",CharsetUtil.UTF_8));

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Message by server : " + ((ByteBuf)msg).toString(CharsetUtil.UTF_8));
    }
}
