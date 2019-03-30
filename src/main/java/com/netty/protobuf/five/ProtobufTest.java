package com.netty.protobuf.five;

import com.google.protobuf.InvalidProtocolBufferException;

public class ProtobufTest {
    public static void main(String[] args) {
        DataInfo.Student student = DataInfo.Student.newBuilder()
                .setName("张三")
                .setAddress("address")
                .setAge(20)
                .build();
        byte[] studentByte = student.toByteArray();

        //
        DataInfo.Student student1;
        try {
            student1 = DataInfo.Student.parseFrom(studentByte);
            System.out.println(student1);
            System.out.println(student1.getName());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

    }
}
