package com.netty.grpc;

import com.netty.proto.MyRequest;
import com.netty.proto.MyResponse;
import com.netty.proto.StudentServiceGrpc;
import io.grpc.stub.StreamObserver;

public class StudentServiceImpl extends StudentServiceGrpc.StudentServiceImplBase {
    /**
     * <pre>
     *    rpc GetRealnameByUsername1(stream MyRequest) returns  (MyResponse){};
     * </pre>
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void getRealnameByUsername(MyRequest request, StreamObserver<MyResponse> responseObserver) {
        //get client info.
        System.out.println("接收到客户端信息:" + request.getUsername());

        //返回客户端消息
        responseObserver.onNext(MyResponse.newBuilder().setRealname("刘明").build());
        // tell client: i'm finished.
        responseObserver.onCompleted();
    }
}
