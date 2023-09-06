package org.xhystudy.rpc.consumer;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @description:
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-24 23:35
 */
@Deprecated
public class RpcReferenceBean implements FactoryBean<Object> {

    private Class<?> interfaceClass;

    private String serviceVersion;

    private long timeout;

    private Object object;

    private String loadBalancerType;

    private String faultTolerantType;

    private long retryCount;

    @Override
    public Object getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    public void init() throws Exception {

        Object object = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcInvokerProxy(serviceVersion,timeout,faultTolerantType,loadBalancerType,retryCount));
        this.object = object;
    }

    public void setRetryCount(long retryCount) {
        this.retryCount = retryCount;
    }

    public void setFaultTolerantType(String faultTolerantType) {
        this.faultTolerantType = faultTolerantType;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setLoadBalancerType(String loadBalancerType) {
        this.loadBalancerType = loadBalancerType;
    }
}
