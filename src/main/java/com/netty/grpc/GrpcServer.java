package com.netty.grpc;

/*
参考
grpc-java/examples/src/main/java/io/grpc/examples/helloworld/HelloWorldServer.java
 */
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {
    private Server server;

    public static void main(String[] args) {
        GrpcServer server = new GrpcServer();

        try {
            server.start();
            server.awaitTermination();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        if (null != this.server) {
            this.server.shutdown();
        }
    }

    private void start() throws IOException {
        this.server = ServerBuilder.forPort(8899).addService(new StudentServiceImpl()).build().start();
        System.out.println("服务器启动");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("jvm will shutdown 1...")));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("jvm will shutdown 2, 我可能跑在１前面")));
    }

    /**
     * 因为它是非阻塞的,所有这里需要防止它退出(thrift是阻塞的)
     * @throws InterruptedException InterruptedException
     */
    private void awaitTermination() throws InterruptedException{
        if (null != this.server) {
            this.server.awaitTermination();
        }
    }
}
