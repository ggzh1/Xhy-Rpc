package org.xhystudy.rpc.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.xhystudy.rpc.Filter.FilterConfig;
import org.xhystudy.rpc.Filter.client.ClientLogFilter;
import org.xhystudy.rpc.annotation.RpcReference;
import org.xhystudy.rpc.config.RpcProperties;
import org.xhystudy.rpc.protocol.serialization.SerializationFactory;
import org.xhystudy.rpc.registry.RegistryFactory;
import org.xhystudy.rpc.router.LoadBalancerFactory;
import org.xhystudy.rpc.utils.PropertiesUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @description: 消费方后置处理器
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-24 23:33
 */
@Configuration
public class ConsumerPostProcessor implements BeanPostProcessor, EnvironmentAware, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);

    RpcProperties rpcProperties;

    /**
     * 从配置文件中读取配置
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        RpcProperties properties = RpcProperties.getInstance();
        PropertiesUtils.init(properties,environment);
        /*String prefix = "rpc.";
        String registerAddr = environment.getProperty(prefix + "register-addr");
        String registerType = environment.getProperty(prefix + "register-type");
        String registerPsw = environment.getProperty(prefix + "register-psw");
        String serialization = environment.getProperty(prefix + "register-serialization");

        properties.setServiceAttachments(PropertyUtil.handle(environment,prefix+"service.",Map.class));
        properties.setClientAttachments(PropertyUtil.handle(environment,prefix+"client.",Map.class));
        properties.setSerialization(serialization);
        properties.setRegisterAddr(registerAddr);
        properties.setRegisterType(registerType);
        properties.setRegisterPsw(registerPsw);*/
        rpcProperties = properties;
        logger.info("读取配置文件成功");

    }

    /**
     * 初始化一些bean
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        SerializationFactory.init();
        RegistryFactory.init();
        LoadBalancerFactory.init();
        FilterConfig.initClientFilter();
    }

    /**
     * 代理层注入
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取所有字段
        final Field[] fields = bean.getClass().getDeclaredFields();
        // 遍历所有字段找到 RpcReference 注解的字段
        for (Field field : fields) {
            if(field.isAnnotationPresent(RpcReference.class)){
                final RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                final Class<?> aClass = field.getType();
                field.setAccessible(true);
                Object object = null;
                try {
                    // 创建代理对象
                    object = Proxy.newProxyInstance(
                            aClass.getClassLoader(),
                            new Class<?>[]{aClass},
                            new RpcInvokerProxy(rpcReference.serviceVersion(),rpcReference.timeout(),rpcReference.faultTolerant(),
                                    rpcReference.loadBalancer(),rpcReference.retryCount()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    // 将代理对象设置给字段
                    field.set(bean,object);
                    field.setAccessible(false);
                    logger.info(beanName + " field:" + field.getName() + "注入成功");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    logger.info(beanName + " field:" + field.getName() + "注入失败");

                }
            }
        }
        return bean;
    }
}
