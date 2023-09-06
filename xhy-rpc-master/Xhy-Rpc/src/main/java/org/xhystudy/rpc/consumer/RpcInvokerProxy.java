package org.xhystudy.rpc.consumer;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.xhystudy.rpc.Filter.FilterConfig;
import org.xhystudy.rpc.Filter.FilterData;
import org.xhystudy.rpc.common.*;
import org.xhystudy.rpc.common.constants.MsgType;
import org.xhystudy.rpc.common.constants.ProtocolConstants;
import org.xhystudy.rpc.config.RpcProperties;
import org.xhystudy.rpc.protocol.MsgHeader;
import org.xhystudy.rpc.protocol.RpcProtocol;
import org.xhystudy.rpc.router.LoadBalancer;
import org.xhystudy.rpc.router.LoadBalancerFactory;
import org.xhystudy.rpc.router.ServiceMetaRes;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.xhystudy.rpc.common.constants.FaultTolerantRules.*;

/**
 * @description: 代理
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-24 23:37
 */
@Slf4j
public class RpcInvokerProxy implements InvocationHandler {

    private String serviceVersion;
    private long timeout;
    private String loadBalancerType;
    private String faultTolerantType;
    private long retryCount;


    public RpcInvokerProxy(String serviceVersion, long timeout,String faultTolerantType,String loadBalancerType,long retryCount) throws Exception {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.loadBalancerType = loadBalancerType;
        this.faultTolerantType = faultTolerantType;
        this.retryCount = retryCount;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        // 构建消息头
        MsgHeader header = new MsgHeader();
        long requestId = RpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);
        final byte[] serialization = RpcProperties.getInstance().getSerialization().getBytes();
        header.setSerializationLen(serialization.length);
        header.setSerializations(serialization);
        header.setMsgType((byte) MsgType.REQUEST.ordinal());
        header.setStatus((byte) 0x1);
        protocol.setHeader(header);

        // 构建请求体
        RpcRequest request = new RpcRequest();
        request.setServiceVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParams(ObjectUtils.isEmpty(args) ? new Object[0] : args);
        request.setServiceAttachments(RpcProperties.getInstance().getServiceAttachments());
        request.setClientAttachments(RpcProperties.getInstance().getClientAttachments());
        // 拦截器的上下文
        final FilterData filterData = new FilterData(request);
        try {
            FilterConfig.getClientBeforeFilterChain().doFilter(filterData);
        }catch (Throwable e){
            throw e;
        }
        protocol.setBody(request);

        RpcConsumer rpcConsumer = new RpcConsumer();




        String serviceName = RpcServiceNameBuilder.buildServiceKey(request.getClassName(), request.getServiceVersion());
        Object[] params = request.getParams();
        // 1.获取负载均衡策略
        final LoadBalancer loadBalancer = LoadBalancerFactory.get(loadBalancerType);

        // 2.根据策略获取对应服务
        final ServiceMetaRes serviceMetaRes = loadBalancer.select(params, serviceName);

        ServiceMeta curServiceMeta = serviceMetaRes.getCurServiceMeta();
        final Collection<ServiceMeta> otherServiceMeta = serviceMetaRes.getOtherServiceMeta();
        long count = 1;
        long retryCount = this.retryCount;
        RpcResponse rpcResponse = null;
        // 重试机制
        while (count <= retryCount ){
            // 处理返回数据
            RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
            // XXXHolder
            RpcRequestHolder.REQUEST_MAP.put(requestId, future);
            try {
                // 发送消息
                rpcConsumer.sendRequest(protocol, curServiceMeta);
                // 等待响应数据返回
                rpcResponse = future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS);
                // 如果有异常并且没有其他服务
                if(rpcResponse.getException()!=null && otherServiceMeta.size() == 0){
                    throw rpcResponse.getException();
                }
                if (rpcResponse.getException()!=null){
                    throw rpcResponse.getException();
                }
                log.info("rpc 调用成功, serviceName: {}",serviceName);
                try {
                    FilterConfig.getClientAfterFilterChain().doFilter(filterData);
                }catch (Throwable e){
                    throw e;
                }
                return rpcResponse.getData();
            }catch (Throwable e){
                String errorMsg = e.toString();
                // todo 这里的容错机制可拓展,留作业自行更改
                switch (faultTolerantType){
                    // 快速失败
                    case FailFast:
                        log.warn("rpc 调用失败,触发 FailFast 策略,异常信息: {}",errorMsg);
                        return rpcResponse.getException();
                    // 故障转移
                    case Failover:
                        log.warn("rpc 调用失败,第{}次重试,异常信息:{}",count,errorMsg);
                        count++;
                        if (!ObjectUtils.isEmpty(otherServiceMeta)){
                            final ServiceMeta next = otherServiceMeta.iterator().next();
                            curServiceMeta = next;
                            otherServiceMeta.remove(next);
                        }else {
                            final String msg = String.format("rpc 调用失败,无服务可用 serviceName: {%s}, 异常信息: {%s}", serviceName, errorMsg);
                            log.warn(msg);
                            throw new RuntimeException(msg);
                        }
                        break;
                    // 忽视这次错误
                    case Failsafe:
                        return null;
                }
            }
        }

        throw new RuntimeException("rpc 调用失败，超过最大重试次数: {}" + retryCount);
    }
}
