package com.netty.nio;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioClient {
    private volatile boolean isQuit = false;

    public static void main(String[] args) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 8899));

            while (true) {
                if (!socketChannel.isOpen()) {
                    System.out.println("服务器已经断开,quit");
                    selector.close();
                    break;
                }
                System.out.println("select");
                selector.select();
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                selectionKeySet.forEach(selectionKey -> {
                    try {
                        final SocketChannel client = (SocketChannel) selectionKey.channel();
                        if (selectionKey.isConnectable()) {
                            System.out.println("connect");
                            if (client.isConnectionPending()) {

                                client.finishConnect();
                                ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                                writeBuffer.put((LocalDateTime.now() + " 连接成功").getBytes());
                                writeBuffer.flip();
                                client.write(writeBuffer);

                                ExecutorService executorService = Executors.newSingleThreadExecutor();
                                executorService.submit(() -> {
                                    while (true) {
                                        writeBuffer.clear();
                                        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                                        String sendMsg = br.readLine();
                                        writeBuffer.put(sendMsg.getBytes());
                                        writeBuffer.flip();
                                        client.write(writeBuffer);
                                    }
                                });
                                client.register(selector, SelectionKey.OP_READ);
                            }
                        } else if (selectionKey.isReadable()) {
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                            int count = channel.read(readBuffer);
                            if (count > 0) {
                                readBuffer.flip();
//                                Charset charset = Charset.forName("utf-8");
//                                String recv = String.valueOf(charset.decode(readBuffer));
                                String recv = new String(readBuffer.array(), 0, count);
                                System.out.println("from server:" + recv);
                            } else if (count < 0) {
                                System.out.println("服务器断开");
                                channel.close();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                selectionKeySet.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
