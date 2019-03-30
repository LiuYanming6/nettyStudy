package com.netty.protobuf.six;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.Random;

import static com.netty.protobuf.six.MyDataInfo.MyMessage.DataType.*;

public class TestClientHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyDataInfo.MyMessage msg) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int randomInt = new Random().nextInt(3);
        MyDataInfo.MyMessage myMessage = null;
        if (0 == randomInt) {
            myMessage = MyDataInfo.MyMessage.newBuilder()
                    .setDataType(MyDataInfo.MyMessage.DataType.PersonType)
                    .setPerson(MyDataInfo.Person.newBuilder()
                            .setName("刘艳明")
                            .setAge(30)
                            .setAddress("上海")
                            .build())
                    .build();
        } else if (1 == randomInt) {
            myMessage = MyDataInfo.MyMessage.newBuilder()
                    .setDataType(DogType)
                    .setDog(MyDataInfo.Dog.newBuilder()
                            .setName("哈雷")
                            .setAge(6).build())
                    .build();
        } else if (2 == randomInt) {
            myMessage = MyDataInfo.MyMessage.newBuilder()
                    .setDataType(CatType)
                    .setCat(MyDataInfo.Cat.newBuilder()
                            .setName("ketty")
                            .setCity("鹤壁")
                            .build())
                    .build();
        }

        if (myMessage != null) {
            ctx.channel().writeAndFlush(myMessage);
        }
    }
}
