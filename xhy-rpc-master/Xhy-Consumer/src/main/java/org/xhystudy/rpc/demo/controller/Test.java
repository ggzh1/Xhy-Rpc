package org.xhystudy.rpc.demo.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xhystudy.rpc.annotation.RpcReference;
import org.xhystudy.rpc.common.constants.FaultTolerantRules;
import org.xhystudy.rpc.common.constants.LoadBalancerRules;
import org.xhystudy.rpc.demo.Test2Service;
import org.xhystudy.rpc.demo.TestService;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @description:
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-25 16:55
 */
@RestController
public class Test {

    @RpcReference(timeout = 10000L,faultTolerant = FaultTolerantRules.Failover,loadBalancer = LoadBalancerRules.RoundRobin)
    TestService testService;

    @RpcReference(loadBalancer = LoadBalancerRules.ConsistentHash)
    Test2Service test2Service;

    /**
     * 轮询
     * 会触发故障转移,提供方模拟异常
     * @param key
     * @return
     */
    @RequestMapping("test/{key}")
    public String test(@PathVariable String key){
        testService.test(key);
        return "test1 ok";
    }

    /**
     * 一致性哈希
     * @param key
     * @return
     */
    @RequestMapping("test2/{key}")
    public String test2(@PathVariable String key){

        return test2Service.test(key);
    }

    /**
     * 轮询,无如何异常
     * @param key
     * @return
     */
    @RequestMapping("test3/{key}")
    public String test3(@PathVariable String key){
        testService.test2(key);
        return "test2 ok";
    }

}
