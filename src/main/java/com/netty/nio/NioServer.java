package com.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/*
client
nc/telnet 192.168.1.100 5001

收到消息后广播出去
 */
public class NioServer {
    private static Map<String, SocketChannel> clientMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8899));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            selectionKeys.forEach(selectionKey -> {
                System.out.println("--------");
                try {
                    final SocketChannel client;
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                        client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);

                        String key = "[" + UUID.randomUUID().toString() + "]";
                        clientMap.put(key, client);

                    } else if (selectionKey.isReadable()) {
                        client = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int count = client.read(byteBuffer);

                        if (count > 0) {
                            //接收消息
                            byteBuffer.flip();
                            Charset charset = Charset.forName("utf-8");
                            String recv = String.valueOf(charset.decode(byteBuffer));
                            System.out.println(client + ":" + recv);

                            String senderKey = null;
                            for (Map.Entry<String, SocketChannel> entry : clientMap.entrySet()) {
                                if (client == entry.getValue()) {
                                    senderKey = entry.getKey();
                                    break;
                                }
                            }

                            // 广播出去
                            for (Map.Entry<String, SocketChannel> entry : clientMap.entrySet()) {
                                SocketChannel value = entry.getValue();

                                ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                                writeBuffer.put((senderKey + ":" + recv).getBytes());

                                writeBuffer.flip();
                                value.write(writeBuffer);
                            }
                        } else if (count == 0) {
                            System.out.println("end of read");
                        } else if (count < 0) {
                            System.out.println("client is closed");
                            client.close();
                            for (Map.Entry<String, SocketChannel> entry : clientMap.entrySet()) {
                                if (client == entry.getValue()) {
                                    clientMap.remove(entry.getKey());
                                    System.out.println(entry + "removed");
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            selectionKeys.clear();
        }
    }
}

