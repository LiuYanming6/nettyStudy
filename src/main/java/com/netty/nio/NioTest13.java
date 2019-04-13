package com.netty.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/*
unicode 是一种编码方式,而UTF是一种存储方式,
UTF-8是Unicode的实现方式之一.
变长1-6字节 英文-1 中文-3
 */
public class NioTest13 {
    public static void main(String[] args) throws IOException {
        Charset.availableCharsets().forEach((k, v) -> {
            System.out.println(k + ": " + v);
        });

        String inputFile = "13in.txt";
        String outputFile = "13out.txt";

        RandomAccessFile inputRandomAccessFile = new RandomAccessFile(inputFile, "r");
        RandomAccessFile outputRandomAccessFile = new RandomAccessFile(outputFile, "rw");

        long inLen = new File(inputFile).length();

        FileChannel inputChannel = inputRandomAccessFile.getChannel();
        FileChannel outputChannel = outputRandomAccessFile.getChannel();

        MappedByteBuffer inputData = inputChannel.map(FileChannel.MapMode.READ_ONLY, 0, inLen);

        Charset charset = Charset.forName("utf-8");
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();

        CharBuffer charBuffer = decoder.decode(inputData);
        ByteBuffer outputData = encoder.encode(charBuffer);
        outputChannel.write(outputData);

        inputRandomAccessFile.close();
        outputRandomAccessFile.close();
    }
}
