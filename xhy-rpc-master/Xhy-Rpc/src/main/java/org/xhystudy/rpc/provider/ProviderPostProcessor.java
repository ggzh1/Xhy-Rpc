package org.xhystudy.rpc.provider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.xhystudy.rpc.Filter.FilterConfig;
import org.xhystudy.rpc.Filter.client.ClientLogFilter;
import org.xhystudy.rpc.annotation.RpcService;
import org.xhystudy.rpc.common.RpcServiceNameBuilder;
import org.xhystudy.rpc.common.ServiceMeta;
import org.xhystudy.rpc.config.RpcProperties;
import org.xhystudy.rpc.poll.ThreadPollFactory;
import org.xhystudy.rpc.protocol.codec.RpcDecoder;
import org.xhystudy.rpc.protocol.codec.RpcEncoder;
import org.xhystudy.rpc.protocol.handler.service.ServiceAfterFilterHandler;
import org.xhystudy.rpc.protocol.handler.service.ServiceBeforeFilterHandler;
import org.xhystudy.rpc.protocol.handler.service.RpcRequestHandler;
import org.xhystudy.rpc.protocol.serialization.SerializationFactory;
import org.xhystudy.rpc.registry.RegistryFactory;
import org.xhystudy.rpc.registry.RegistryService;
import org.xhystudy.rpc.router.LoadBalancerFactory;
import org.xhystudy.rpc.utils.PropertiesUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 服务提供方后置处理器
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439?spm_id_from=333.1007.0.0
 * @CreateTime: 2023-04-24 15:43  XXXXAware
 */
public class ProviderPostProcessor implements InitializingBean,BeanPostProcessor, EnvironmentAware {


    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);

    RpcProperties rpcProperties;

    private static String serverAddress;

    static {
        try {
            serverAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private final Map<String, Object> rpcServiceMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {

        Thread t = new Thread(() -> {
            try {
                startRpcServer();
            } catch (Exception e) {
                logger.error("start rpc server error.", e);
            }
        });
        t.setDaemon(true);
        t.start();
        SerializationFactory.init();
        RegistryFactory.init();
        LoadBalancerFactory.init();
        FilterConfig.initServiceFilter();
        ThreadPollFactory.setRpcServiceMap(rpcServiceMap);
    }

    private void startRpcServer() throws InterruptedException {
        int serverPort = rpcProperties.getPort();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcDecoder())
                                    .addLast(new ServiceBeforeFilterHandler())
                                    .addLast(new RpcRequestHandler())
                                    .addLast(new ServiceAfterFilterHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(this.serverAddress, serverPort).sync();
            logger.info("server addr {} started on port {}", this.serverAddress, serverPort);
            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * 服务注册
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        // 找到bean上带有 RpcService 注解的类
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 可能会有多个接口,默认选择第一个接口
            String serviceName = beanClass.getInterfaces()[0].getName();
            if (!rpcService.serviceInterface().equals(void.class)){
                serviceName = rpcService.serviceInterface().getName();
            }
            String serviceVersion = rpcService.serviceVersion();
            try {
                // 服务注册
                int servicePort = rpcProperties.getPort();
                // 获取注册中心 ioc
                RegistryService registryService = RegistryFactory.get(rpcProperties.getRegisterType());
                ServiceMeta serviceMeta = new ServiceMeta();
                serviceMeta.setServiceAddr(serverAddress);
                serviceMeta.setServicePort(servicePort);
                serviceMeta.setServiceVersion(serviceVersion);
                serviceMeta.setServiceName(serviceName);
                registryService.register(serviceMeta);
                // 缓存
                rpcServiceMap.put(RpcServiceNameBuilder.buildServiceKey(serviceMeta.getServiceName(),serviceMeta.getServiceVersion()), bean);
                logger.info("register server {} version {}",serviceName,serviceVersion);
            } catch (Exception e) {
                logger.error("failed to register service {}",  serviceVersion, e);
            }
        }
        return bean;
    }

    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties properties = RpcProperties.getInstance();
        PropertiesUtils.init(properties,environment);
        /*String prefix = "rpc.";
        Integer port = Integer.valueOf(environment.getProperty(prefix + "port"));
        String registerAddr = environment.getProperty(prefix + "register-addr");
        String registerType = environment.getProperty(prefix + "register-type");
        String registerPsw = environment.getProperty(prefix + "register-psw");
        String serialization = environment.getProperty(prefix + "register-serialization");

        RpcProperties properties = RpcProperties.getInstance();
        // 额外拓展的参数
        properties.setServiceAttachments(PropertyUtil.handle(environment,prefix+"service.",Map.class));
        properties.setClientAttachments(PropertyUtil.handle(environment,prefix+"client.",Map.class));
        properties.setPort(port);
        properties.setSerialization(serialization);
        properties.setRegisterAddr(registerAddr);
        properties.setRegisterType(registerType);
        properties.setRegisterPsw(registerPsw);*/
        rpcProperties = properties;
        logger.info("读取配置文件成功");
    }
}
