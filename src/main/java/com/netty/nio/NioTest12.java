package com.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/*
Java NIO Selector 选择器 一个单独的线程可以管理多个channel，从而管理多个网络连接
现代的操作系统和CPU在多任务方面表现的越来越好，所以多线程的开销随着时间的推移，变得越来越小了
client
nc/telnet 192.168.1.100 5001

echo server
 */
public class NioTest12 {
    public static void main(String[] args) {
        int[] ports = new int[5];
        ports[0] = 5000;
        ports[1] = 5001;
        ports[2] = 5002;
        ports[3] = 5003;
        ports[4] = 5004;

        Selector selector;
        try {
            selector = Selector.open();
//            System.out.println(SelectorProvider.provider().getClass());sun.nio.ch.EPollSelectorProvider
            for (int i = 0; i < ports.length; i++) {
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
                InetSocketAddress address = new InetSocketAddress(ports[i]);

                serverSocketChannel.bind(address);
//                ServerSocket serverSocket = serverSocketChannel.socket();
//                serverSocket.bind(address);

                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                System.out.println("监听端口:" + ports[i]);
            }

            while (true) {
                int numbers = selector.select();
                System.out.println("num=" + numbers);

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
//                System.out.println("selectionKeys: " + selectionKeys);

                Iterator<SelectionKey> iter = selectionKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey selectionKey = iter.next();
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();

                        SocketChannel channel = serverSocketChannel.accept();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);

                        System.out.println("获取客户端连接:");
                    } else if (selectionKey.isReadable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        int byteRead = 0;
                        ByteBuffer byteBuffer;
                        while (true) {
                            byteBuffer = ByteBuffer.allocate(512);
                            byteBuffer.clear();
                            int read = channel.read(byteBuffer);
                            System.out.println("读取: " + read + ",来自于:" + channel);
                            if (read == 0) {
                                System.out.println("消息对到头了");
                                break;
                            } else if (read < 0) {
                                System.out.println("客户端已经关闭");
                                channel.close();
                                break;
                            }

                            byteBuffer.flip();
                            byteBuffer.mark();

                            System.out.print("客户消息:");
                            while (byteBuffer.remaining() > 0) {
                                System.out.print((char) byteBuffer.get());
                            }
                            System.out.println();

                            byteBuffer.reset();
                            channel.write(byteBuffer);
                            byteRead += read;
                        }
                    }
                    iter.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
