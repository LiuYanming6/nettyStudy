package com.netty.grpc;

import com.netty.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.time.LocalDateTime;
import java.util.Iterator;

/*
参考
grpc-java/examples/src/main/java/io/grpc/examples/helloworld/HelloWorldClient.java
 */
public class GrpcClient {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 8899)
                .usePlaintext().build(); //未加密的, 默认是TLS
        //blockingStub 是同步的, 1,2 used
        StudentServiceGrpc.StudentServiceBlockingStub blockingStub = StudentServiceGrpc
                .newBlockingStub(managedChannel);
        //stub 针对流式的 异步的
        StudentServiceGrpc.StudentServiceStub stub = StudentServiceGrpc.newStub(managedChannel);

        System.out.println("------------------１-----------------");
        MyResponse myResponse = blockingStub.getRealnameByUsername(MyRequest.newBuilder().setUsername("张三").build());

        System.out.println(myResponse.getRealname());


        System.out.println("------------------2-----------------");
        Iterator<StudentResponse> iterator = blockingStub.getStudentsByAge(StudentRequest.newBuilder().setAge(26).build());

//        Stream.generate(iterator::next).takeWhile(i -> iterator.hasNext()).forEach(System.out::println);//java9

        while (iterator.hasNext()) {
            StudentResponse studentResponse = iterator.next();
            System.out.println(studentResponse.getName() + "," + studentResponse.getAge() + "," + studentResponse.getCity());
        }


        System.out.println("-------------------3----------------");
        StreamObserver<StudentResponseList> studentResponseListStreamObserver = new StreamObserver<StudentResponseList>() {
            @Override
            public void onNext(StudentResponseList value) {
                value.getStudentResponseList().forEach(studentResponse -> {
                    System.out.println(studentResponse.getName());
                    System.out.println(studentResponse.getAge());
                    System.out.println(studentResponse.getCity());
                    System.out.println("*****onNext end*****");
                });
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }
        };
        StreamObserver<StudentRequest> studentRequestStreamObserver = stub.getStudentsWrapperByAges(studentResponseListStreamObserver);

        studentRequestStreamObserver.onNext(StudentRequest.newBuilder().setAge(12).build());
        studentRequestStreamObserver.onNext(StudentRequest.newBuilder().setAge(13).build());
        studentRequestStreamObserver.onNext(StudentRequest.newBuilder().setAge(14).build());
        studentRequestStreamObserver.onNext(StudentRequest.newBuilder().setAge(15).build());
        studentRequestStreamObserver.onNext(StudentRequest.newBuilder().setAge(16).build());

        studentRequestStreamObserver.onCompleted();
        // 因为stub是异步的,马上就退出了,来不及收到服务器的消息
        // so we wait five second.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 因为添加了下面的代码，上面的sleep实际是不需要了

        System.out.println("-------------------4 bidirectional stream----------------");
        StreamObserver<StreamRequest> requestStreamObserver = stub.biTalk(new StreamObserver<StreamResponse>() {
            @Override
            public void onNext(StreamResponse value) {
                System.out.println(value.getResponseInfo());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }
        });

        for (int i = 0; i < 10; i++) {
            requestStreamObserver.onNext(StreamRequest.newBuilder().setRequestInfo(LocalDateTime.now().toString()).build());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
