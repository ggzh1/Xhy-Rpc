package org.xhystudy.rpc.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.xhystudy.rpc.annotation.EnableConsumerRpc;

@SpringBootApplication
@EnableConsumerRpc
public class RpcConsumerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcConsumerDemoApplication.class, args);
    }


}
