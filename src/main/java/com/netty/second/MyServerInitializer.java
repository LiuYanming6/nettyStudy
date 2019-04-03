package com.netty.second;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

class MyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = MyUtil.getCommonPipeline(ch);

        //our's
        pipeline.addLast(new MyServerHandler());
    }
}
