package com.netty.grpc;

import com.netty.proto.MyRequest;
import com.netty.proto.MyResponse;
import com.netty.proto.StudentServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/*
参考
grpc-java/examples/src/main/java/io/grpc/examples/helloworld/HelloWorldClient.java
 */
public class GrpcClient {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 8899)
                .usePlaintext().build(); //未加密的, 默认是TLS
        StudentServiceGrpc.StudentServiceBlockingStub blockingStub = StudentServiceGrpc
                .newBlockingStub(managedChannel);
        MyResponse myResponse = blockingStub.getRealnameByUsername(MyRequest.newBuilder().setUsername("张三").build());

        System.out.println(myResponse.getRealname());
    }
}
