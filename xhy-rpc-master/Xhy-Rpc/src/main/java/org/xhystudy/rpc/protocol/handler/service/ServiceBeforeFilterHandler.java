package org.xhystudy.rpc.protocol.handler.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhystudy.rpc.Filter.FilterConfig;
import org.xhystudy.rpc.Filter.FilterData;
import org.xhystudy.rpc.Filter.client.ClientLogFilter;
import org.xhystudy.rpc.common.RpcRequest;
import org.xhystudy.rpc.common.RpcResponse;
import org.xhystudy.rpc.common.constants.MsgStatus;
import org.xhystudy.rpc.protocol.MsgHeader;
import org.xhystudy.rpc.protocol.RpcProtocol;

/**
 * @description: 前置拦截器
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-08-08 22:35
 */
public class ServiceBeforeFilterHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private Logger logger = LoggerFactory.getLogger(ServiceBeforeFilterHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        final RpcRequest request = protocol.getBody();
        final FilterData filterData = new FilterData(request);
        RpcResponse response = new RpcResponse();
        MsgHeader header = protocol.getHeader();

        try {
            FilterConfig.getServiceBeforeFilterChain().doFilter(filterData);

        } catch (Exception e) {
            RpcProtocol<RpcResponse> resProtocol = new RpcProtocol<>();
            header.setStatus((byte) MsgStatus.FAILED.ordinal());
            response.setException(e);
            logger.error("before process request {} error", header.getRequestId(), e);
            resProtocol.setHeader(header);
            resProtocol.setBody(response);
            ctx.writeAndFlush(resProtocol);
            return;
        }
        ctx.fireChannelRead(protocol);
    }
}
