package org.xhystudy.rpc.registry;

import org.xhystudy.rpc.common.ServiceMeta;

import java.io.IOException;
import java.util.List;

/**
 * @description: 注册中心接口
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-24 23:38
 */
public interface RegistryService {

    /**
     * 服务注册
     * @param serviceMeta
     * @throws Exception
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务注销
     * @param serviceMeta
     * @throws Exception
     */
    void unRegister(ServiceMeta serviceMeta) throws Exception;


    /**
     * 获取 serviceName 下的所有服务
     * @param serviceName
     * @return
     */
    List<ServiceMeta> discoveries(String serviceName);
    /**
     * 关闭
     * @throws IOException
     */
    void destroy() throws IOException;

}
