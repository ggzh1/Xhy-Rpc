package org.xhystudy.rpc.registry;

import org.xhystudy.rpc.spi.ExtensionLoader;

/**
 * @description: 注册工厂
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-24 23:39
 */
public class RegistryFactory {

    public static RegistryService get(String registryService) throws Exception {
        return ExtensionLoader.getInstance().get(registryService);
    }

    public static void init() throws Exception {
        ExtensionLoader.getInstance().loadExtension(RegistryService.class);
    }

}
