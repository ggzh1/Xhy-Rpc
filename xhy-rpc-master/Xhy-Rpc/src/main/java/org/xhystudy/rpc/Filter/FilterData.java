package org.xhystudy.rpc.Filter;

import org.xhystudy.rpc.common.RpcRequest;
import org.xhystudy.rpc.common.RpcResponse;

import java.util.Arrays;
import java.util.Map;

/**
 * @description: 上下文数据
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-08-03 11:34
 */
public class FilterData {


    private String serviceVersion;
    private long timeout;
    private long retryCount;
    private String className;
    private String methodName;
    private Object[] args;
    private Map<String,Object> serviceAttachments;
    private Map<String,Object> clientAttachments;
    private RpcResponse data; // 执行业务逻辑后的数据

    public FilterData(RpcRequest request) {
        this.args = request.getParams();
        this.className = request.getClassName();
        this.methodName = request.getMethodName();
        this.serviceVersion = request.getServiceVersion();
        this.serviceAttachments = request.getServiceAttachments();
        this.clientAttachments = request.getClientAttachments();
    }
    public FilterData(){

    }

    public RpcResponse getData() {
        return data;
    }

    public void setData(RpcResponse data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "调用: Class: " + className + " Method: " + methodName + " args: " + Arrays.toString(args) +" Version: " + serviceVersion
                +" Timeout: " + timeout +" ServiceAttachments: " + serviceAttachments +
                " ClientAttachments: " + clientAttachments;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(long retryCount) {
        this.retryCount = retryCount;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
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
}
