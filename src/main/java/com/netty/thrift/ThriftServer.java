package com.netty.thrift;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import thrift.generated.PersonService;

public class ThriftServer {
    public static void main(String[] args) {
        TNonblockingServerSocket socket = null;
        THsHaServer.Args arg;
        PersonService.Processor<PersonServiceImg> processor;    //处理器
        TServer server = null;

        try {
            socket = new TNonblockingServerSocket(8899);
            arg = new THsHaServer.Args(socket).minWorkerThreads(2).maxWorkerThreads(4);
            processor = new PersonService.Processor<>(new PersonServiceImg());

            /* 数据传输格式
            TBinaryProtocol
            TCompactProtocol  常用
            TJSONProtocol
            TSimpleJSONProtocol
             */
            arg.protocolFactory(new TCompactProtocol.Factory());   //二进制压缩协议

            /*
            数据传输方式
            ＴSocket 阻塞式
            TFramedTransport  以frame 为单位进行传输,　非阻塞式服务中使用
            TFileTransport 以文件形式进行传输
            ...
             */
            arg.transportFactory(new TFastFramedTransport.Factory());//底层传输
            arg.processorFactory(new TProcessorFactory(processor));

            /*
            服务模型
            TSimpleServer 单线程,常用于测试
            TThreadPoolServer 多线程,使用标准的阻塞式IO
            TNonblockingServer 多线程非阻塞式IO　需TFramedTransport
            THsHaServer 引入了线程池去处理,把读写任务放到线程池去处理An extension of the TNonblockingServer to a Half-Sync/Half-Async server.
 * Like TNonblockingServer, it relies on the use of TFramedTransport.
             */
            server = new THsHaServer(arg);

            System.out.println("Thrift Server Started");

            //异步非阻塞
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
