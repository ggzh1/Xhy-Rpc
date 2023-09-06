# Xhy-Rpc

#### 介绍
用于学习手写一个Rpc

#### 软件架构
SpringBoot+Netty+Redis/ZK


#### 安装教程

1.  mvn install
2.  引入依赖


```
        <dependency>
            <groupId>org.xhystudy</groupId>
            <artifactId>Xhy-Rpc</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
```


#### 使用说明
1.服务提供方引入注解 @EnableProviderRpc


```
@SpringBootApplication
@EnableProviderRpc
public class RpcProviderDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcProviderDemoApplication.class, args);
    }
}
```

2.在服务消费方引入注解 @EnableConsumerRpc

```
@SpringBootApplication
@EnableConsumerRpc
public class RpcConsumerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcConsumerDemoApplication.class, args);
    }


}

```

3.服务消费方配置文件
配置中心地址以及使用的配置中心


```
rpc.register-addr=127.0.0.1:2181
rpc.register-type=ZOOKEEPER
```


4.服务提供方配置文件

```
rpc.port=5555
rpc.register-addr=127.0.0.1:2181
rpc.register-type=ZOOKEEPER
```

5.服务提供方注解 @RpcService

```
@RpcService
public class Test2ServiceImpl implements Test2Service {
    @Override
    public String test(String key) {
        System.out.println("服务提供1 test2 测试成功 :" + key);
        return key;
    }
}
```

6.服务消费方注解 @RpcReference,配置可以默认不配置。将使用默认配置


```
    @RpcReference(loadBalancerType = LoadBalancerType.RoundRobin,retryCount = 5L)
    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    TestService testService;

```

默认配置

```
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Autowired
public @interface RpcReference {

    String serviceVersion() default "1.0";

    long timeout() default 5000;

    LoadBalancerType loadBalancerType() default LoadBalancerType.RoundRobin;

    FaultTolerantType faultTolerantType() default FaultTolerantType.Failover;

    long retryCount() default 3;
}
```


该项目仅供学习使用

视频地址：[b站](https://www.bilibili.com/video/BV1Qc411N73Z/?vd_source=884a1f9702167e8936a8d6d773a193ae)

