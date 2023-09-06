package org.xhystudy.rpc.tolerant;

/**
 * @description: 容错策略
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-05-03 21:15
 */
public interface FaultTolerantStrategy {

    void handler();

}
