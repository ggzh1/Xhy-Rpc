package org.xhystudy.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @description: 服务提供方
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-24 21:16
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {


    /**
     * 指定实现方,默认为实现接口中第一个
     * @return
     */
    Class<?> serviceInterface() default void.class;

    /**
     * 版本
     * @return
     */
    String serviceVersion() default "1.0";
}
