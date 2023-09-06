package org.xhystudy.rpc.annotation;

import org.springframework.context.annotation.Import;
import org.xhystudy.rpc.consumer.ConsumerPostProcessor;

import java.lang.annotation.*;

/**
 * @description: 开启调用方自动装配
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-24 15:42
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(ConsumerPostProcessor.class)
public @interface EnableConsumerRpc {


}
