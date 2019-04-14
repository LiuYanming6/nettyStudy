package com.netty.zerocopy;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;

/*
user        kernel
send ->
copy from kernel
copy to kernel
 */
public class OldIOClient {
    static final String FILE_PATH = "/home/liu/Downloads/node-v10.15.3-linux-x64.tar.xz";

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8899);
        InputStream inputStream = new FileInputStream(FILE_PATH);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        byte[] buffer = new byte[4096];
        long readCount;
        long total = 0;
        long startTime = System.currentTimeMillis();

        while ((readCount = inputStream.read(buffer)) >= 0) {
            total += readCount;
            dataOutputStream.write(buffer);
        }

        System.out.println("发送字节:" + total + "耗时" + (System.currentTimeMillis() - startTime));
        //发送字节:12309200耗时14

        dataOutputStream.close();
        inputStream.close();
        socket.close();
    }
}
