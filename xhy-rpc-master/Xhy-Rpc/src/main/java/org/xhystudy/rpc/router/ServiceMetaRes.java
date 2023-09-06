package org.xhystudy.rpc.router;

import org.xhystudy.rpc.common.ServiceMeta;

import java.util.Collection;

/**
 * @description:
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-08-04 10:54
 */
public class ServiceMetaRes {

    // 当前服务节点
    private ServiceMeta curServiceMeta;

    // 剩余服务节点
    private Collection<ServiceMeta> otherServiceMeta;

    public Collection<ServiceMeta> getOtherServiceMeta() {
        return otherServiceMeta;
    }

    public ServiceMeta getCurServiceMeta() {
        return curServiceMeta;
    }

    public static ServiceMetaRes build(ServiceMeta curServiceMeta, Collection<ServiceMeta> otherServiceMeta){
        final ServiceMetaRes serviceMetaRes = new ServiceMetaRes();
        serviceMetaRes.curServiceMeta = curServiceMeta;
        otherServiceMeta.remove(curServiceMeta);
        serviceMetaRes.otherServiceMeta = otherServiceMeta;
        return serviceMetaRes;
    }

}
