package org.xhystudy.rpc.Filter.service;

import org.xhystudy.rpc.Filter.FilterData;
import org.xhystudy.rpc.Filter.ServiceBeforeFilter;
import org.xhystudy.rpc.config.RpcProperties;

import java.util.Map;

/**
 * @description: token拦截器
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-08-03 12:07
 */
public class ServiceTokenFilter implements ServiceBeforeFilter {

    @Override
    public void doFilter(FilterData filterData) {
        final Map<String, Object> attachments = filterData.getClientAttachments();
        final Map<String, Object> serviceAttachments = RpcProperties.getInstance().getServiceAttachments();
        if (!attachments.getOrDefault("token","").equals(serviceAttachments.getOrDefault("token",""))){
            throw new IllegalArgumentException("token不正确");
        }
    }

}
