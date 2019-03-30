namespace java thrift.generated

# 我习惯用java的类型名字,所以重新定义下
//thrift --gen java src/thrift/data.thrift
/*
将当前目录　gen-java/thrift 移到java下
 */
typedef i16 short
typedef i32 int
typedef i64 long
typedef bool boolean
typedef string String

struct Person {
    1: optional String username,
    2: optional int age,
    3: optional boolean married
}

// 可以由服务器抛给客户端
exception DataException {
    1: optional String message,
    2: optional String callStack,
    3: optional String date
}

service PersonService {
    Person getPersonByUsername(1: required String username) throws (1: DataException dataException),
    void savePerson(1: required Person person) throws (1: DataException dataException)
}
