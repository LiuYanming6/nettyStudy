## todo
### 怎样优雅的管理 grpc定义的idl文件生成的中间代码
1. 生成的中间代码在build目录里,需要复制到源码树里，并删除原来的
./gradlew clean
./gradlew generateProto
mv -f ./build/generated/source/proto/main/grpc/com/netty/proto/* src/main/java/com/netty/proto/
mv -f ./build/generated/source/proto/main/java/com/netty/proto/* src/main/java/com/netty/proto/

2. generateProto 依赖 'com.google.protobuf:protobuf-gradle-plugin:0.8.5'(build.gradle)
能不能修改这个插件的源码，使得生成的文件直接到源码树
~/.gradle/caches/modules-2/files-2.1/com.google.protobuf/protobuf-gradle-plugin/0.8.5/ 

3. 生成的中间代码, 利用git subtree维护
