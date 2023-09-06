package org.xhystudy.rpc.router;

/**
 * @description: 负载均衡,根据负载均衡获取对应的服务节点(负载均衡包装服务节点)
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-25 11:13
 */
public interface LoadBalancer<T> {

     /**
      * 选择负载均衡策略
      * @param params 入参,可自定义拿到入参后自行处理负载策略
      * @param serviceName 服务key
      * @return 当前服务节点以及其他节点，用于给容错使用
      */
     ServiceMetaRes select(Object[] params,String serviceName);

}
