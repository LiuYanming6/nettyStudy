
### 怎样优雅的管理 grpc定义的idl文件生成的中间代码
1. 生成的中间代码在build目录里,需要复制到源码树里，并删除原来的
./gradlew clean
./gradlew generateProto
mv -f ./build/generated/source/proto/main/grpc/com/netty/proto/* src/main/java/com/netty/proto/
mv -f ./build/generated/source/proto/main/java/com/netty/proto/* src/main/java/com/netty/proto/

2. generateProto 依赖 'com.google.protobuf:protobuf-gradle-plugin:0.8.5'(build.gradle)
能不能修改这个插件的源码，使得生成的文件直接到源码树
~/.gradle/caches/modules-2/files-2.1/com.google.protobuf/protobuf-gradle-plugin/0.8.5/ 
查看源码后可以修改build.gradle中generateProtoTasks,这样就优雅多了
    generateProtoTasks.generatedFilesBaseDir = 'src' //default 'build/gen..'
    generateProtoTasks {
        all()*.plugins {
            grpc {
                outputSubDir = 'java' //default 'grpc'
            }
        }
    }
或者参考插件官网介绍https://github.com/google/protobuf-gradle-plugin
3. 也有大师建议中间代码不应和源码放在一起
`
sourceSets {
    main {
        proto {
            srcDir 'src/main/proto'
        }
        java {
            // include self written and generated code
            srcDirs 'src/main/java', 'generated-sources/main/java'            
        }
    }
    // remove the test configuration - at least in your example you don't have a special test proto file
}
protobuf {
    // Configure the protoc executable
    protoc {
        // Download from repositories
        artifact = 'com.google.protobuf:protoc:3.0.0-alpha-3'
    }
    generateProtoTasks.generatedFilesBaseDir = 'generated-sources'
    generateProtoTasks {
        // all() returns the collection of all protoc tasks
        all().each { task ->
            // Here you can configure the task
        }
        // In addition to all(), you may get the task collection by various
        // criteria:
        // (Java only) returns tasks for a sourceSet
        ofSourceSet('main')
    }   
}
`

4. 生成的中间代码, 利用git subtree维护

### 遇到问题首先看官网介绍, 解决不了再看源码. 网上的搜索到的别人的回答由于版本和时间差异,只能作为参考.