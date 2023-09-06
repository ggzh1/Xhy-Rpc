package org.xhystudy.rpc.config;

import org.springframework.core.env.Environment;
import org.xhystudy.rpc.annotation.PropertiesField;
import org.xhystudy.rpc.annotation.PropertiesPrefix;
import org.xhystudy.rpc.common.constants.RegistryRules;
import org.xhystudy.rpc.common.constants.SerializationRules;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:  配置信息
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-25 10:47
 */
@PropertiesPrefix("rpc")
public class RpcProperties {


    /**
     * netty 端口
     */
    @PropertiesField
    private Integer port;

    /**
     * 注册中心地址
     */
    @PropertiesField
    private String registerAddr;

    /**
     * 注册中心类型
     */
    @PropertiesField
    private String registerType = RegistryRules.ZOOKEEPER;

    /**
     * 注册中心密码
     */
    @PropertiesField
    private String registerPsw;

    /**
     * 序列化
     */
    @PropertiesField
    private String serialization = SerializationRules.JSON;

    /**
     * 服务端额外配置数据
     */
    @PropertiesField("service")
    private Map<String,Object> serviceAttachments = new HashMap<>();

    /**
     * 客户端额外配置数据
     */
    @PropertiesField("client")
    private Map<String,Object> clientAttachments = new HashMap<>();

    static RpcProperties rpcProperties;

    public static RpcProperties getInstance(){
            if (rpcProperties == null){
                rpcProperties = new RpcProperties();
            }
        return rpcProperties;
    }
    private RpcProperties(){}


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        if(registerType == null || registerType.equals("")){
            registerType = RegistryRules.ZOOKEEPER;
        }
        this.registerType = registerType;
    }

    public String getRegisterPsw() {
        return registerPsw;
    }

    public void setRegisterPsw(String registerPsw) {
        this.registerPsw = registerPsw;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        if(serialization == null || serialization.equals("")){
            serialization = SerializationRules.JSON;
        }
        this.serialization = serialization;
    }

    public Map<String, Object> getServiceAttachments() {
        return serviceAttachments;
    }

    public void setServiceAttachments(Map<String, Object> serviceAttachments) {
        this.serviceAttachments = serviceAttachments;
    }

    public Map<String, Object> getClientAttachments() {
        return clientAttachments;
    }

    public void setClientAttachments(Map<String, Object> clientAttachments) {
        this.clientAttachments = clientAttachments;
    }




    public String getRegisterAddr() {
        return registerAddr;
    }

    public void setRegisterAddr(String registerAddr) {
        this.registerAddr = registerAddr;
    }

    /**
     * 做一个能够解析任意对象属性的工具类
     * @param environment
     */
    public static void init(Environment environment){

    }
}
