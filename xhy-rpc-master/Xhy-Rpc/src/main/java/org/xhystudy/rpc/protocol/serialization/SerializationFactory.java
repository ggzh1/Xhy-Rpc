package org.xhystudy.rpc.protocol.serialization;

import org.xhystudy.rpc.spi.ExtensionLoader;

/**
 * @description: 序列化工厂
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-30 12:42
 */
public class SerializationFactory {


    public static RpcSerialization get(String serialization) throws Exception {

        return ExtensionLoader.getInstance().get(serialization);

    }

    public static void init() throws Exception {
        ExtensionLoader.getInstance().loadExtension(RpcSerialization.class);
    }
}
