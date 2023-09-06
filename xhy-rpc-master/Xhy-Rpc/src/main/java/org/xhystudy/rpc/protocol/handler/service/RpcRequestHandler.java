package org.xhystudy.rpc.protocol.handler.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.xhystudy.rpc.common.RpcRequest;
import org.xhystudy.rpc.poll.ThreadPollFactory;
import org.xhystudy.rpc.protocol.RpcProtocol;

/**
 * @description: 处理消费方发送数据并且调用方法
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-30 13:16
 */
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {


    public RpcRequestHandler() {}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        ThreadPollFactory.submitRequest(ctx,protocol);
    }

}

