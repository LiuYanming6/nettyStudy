package com.netty.zerocopy;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/*
零拷贝
 */
public class NewClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(true);
        socketChannel.connect(new InetSocketAddress("localhost", 8899));

        FileChannel fileChannel = new FileInputStream(OldIOClient.FILE_PATH).getChannel();
        long startTime = System.currentTimeMillis();
        /* javadoc
         * This method is potentially much more efficient than a simple loop
         * that reads from this channel and writes to the target channel.  Many
         * operating systems can transfer bytes directly from the filesystem cache
         * to the target channel without actually copying them.
         */
        long transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);

        System.out.println("发送字节:" + transferCount + "耗时" + (System.currentTimeMillis() - startTime));
        //发送字节:12309200耗时5
    }
}
