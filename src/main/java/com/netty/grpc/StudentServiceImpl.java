package com.netty.grpc;

import com.netty.proto.*;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class StudentServiceImpl extends StudentServiceGrpc.StudentServiceImplBase {
    /**
     * <pre>
     *    rpc GetRealnameByUsername1(stream MyRequest) returns  (MyResponse){};
     * </pre>
     *
     * @param request 客户端请求
     * @param responseObserver responseObserver
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

    /**
     * <pre>
     * return a stream
     * </pre>
     *
     * @param request          StudentRequest
     * @param responseObserver responseObserver
     */
    @Override
    public void getStudentsByAge(StudentRequest request, StreamObserver<StudentResponse> responseObserver) {
        System.out.println("接收到客户端信息:" + request.getAge());

        // 流式数据
        responseObserver.onNext(StudentResponse.newBuilder().setName("张三").setAge(20).setCity("上海").build());
        responseObserver.onNext(StudentResponse.newBuilder().setName("李四").setAge(24).setCity("深圳").build());
        responseObserver.onNext(StudentResponse.newBuilder().setName("王五").setAge(25).setCity("郑州").build());
        responseObserver.onNext(StudentResponse.newBuilder().setName("赵柳").setAge(26).setCity("北京").build());

        // i'm finished.
        responseObserver.onCompleted();
    }

    /**
     * <pre>
     * client send a stream and server return a object
     * </pre>
     *
     * @param responseObserver responseObserver
     */
    @Override
    public StreamObserver<StudentRequest> getStudentsWrapperByAges(StreamObserver<StudentResponseList> responseObserver) {
        return new StreamObserver<StudentRequest>() {
            /**
             * Receives a value from the stream.
             *
             * <p>Can be called many times but is never called after {@link #onError(Throwable)} or {@link
             * #onCompleted()} are called.
             *
             * <p>Unary calls must invoke onNext at most once.  Clients may invoke onNext at most once for
             * server streaming calls, but may receive many onNext callbacks.  Servers may invoke onNext at
             * most once for client streaming calls, but may receive many onNext callbacks.
             *
             * <p>If an exception is thrown by an implementation the caller is expected to terminate the
             * stream by calling {@link #onError(Throwable)} with the caught exception prior to
             * propagating it.
             *
             * @param value the value passed to the stream
             */
            @Override
            public void onNext(StudentRequest value) {
                System.out.println("OnNext:" + value.getAge());
            }

            /**
             * Receives a terminating error from the stream.
             *
             * <p>May only be called once and if called it must be the last method called. In particular if an
             * exception is thrown by an implementation of {@code onError} no further calls to any method are
             * allowed.
             *
             * <p>{@code t} should be a {@link StatusException} or {@link
             * StatusRuntimeException}, but other {@code Throwable} types are possible. Callers should
             * generally convert from a {@link Status} via {@link Status#asException()} or
             * {@link Status#asRuntimeException()}. Implementations should generally convert to a
             * {@code Status} via {@link Status#fromThrowable(Throwable)}.
             *
             * @param t the error occurred on the stream
             */
            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            /**
             * Receives a notification of successful stream completion.
             *
             * <p>May only be called once and if called it must be the last method called. In particular if an
             * exception is thrown by an implementation of {@code onCompleted} no further calls to any method
             * are allowed.
             */
            @Override
            public void onCompleted() {
                StudentResponse studentResponse1 = StudentResponse.newBuilder().setName("张三").setAge(11).setCity("洛阳").build();
                StudentResponse studentResponse2 = StudentResponse.newBuilder().setName("jack").setAge(12).setCity("洛阳").build();
                StudentResponse studentResponse3 = StudentResponse.newBuilder().setName("john").setAge(13).setCity("金额比").build();

                StudentResponseList studentResponseList = StudentResponseList.newBuilder()
                        .addStudentResponse(studentResponse1)
                        .addStudentResponse(studentResponse2)
                        .addStudentResponse(studentResponse3)
                        .build();
                responseObserver.onNext(studentResponseList);
                responseObserver.onCompleted();
            }
        };
    }

    /**
     * <pre>
     * bidirectional stream
     * </pre>
     *
     * @param responseObserver responseObserver
     */
    @Override
    public StreamObserver<StreamRequest> biTalk(StreamObserver<StreamResponse> responseObserver) {
        return new StreamObserver<StreamRequest>() {
            @Override
            public void onNext(StreamRequest value) {
                System.out.println(value.getRequestInfo());

                responseObserver.onNext(StreamResponse.newBuilder().setResponseInfo(UUID.randomUUID().toString()).build());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                //一般双向流中，一方关闭后，另一端也要关闭
                responseObserver.onCompleted();
            }
        };
    }
}
