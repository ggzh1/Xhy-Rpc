package org.xhystudy.rpc.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.xhystudy.rpc.common.RpcRequest;
import org.xhystudy.rpc.common.RpcResponse;
import org.xhystudy.rpc.protocol.MsgHeader;
import org.xhystudy.rpc.common.constants.MsgType;
import org.xhystudy.rpc.common.constants.ProtocolConstants;
import org.xhystudy.rpc.protocol.RpcProtocol;
import org.xhystudy.rpc.protocol.serialization.RpcSerialization;
import org.xhystudy.rpc.protocol.serialization.SerializationFactory;

import java.util.List;

/**
 * @description: 解码器
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-25 14:04
 */
public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

        // 如果可读字节数少于协议头长度，说明还没有接收完整个协议头，直接返回
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }
        // 标记当前读取位置，便于后面回退
        in.markReaderIndex();

        // 读取魔数字段
        short magic = in.readShort();
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }
        // 读取版本字段
        byte version = in.readByte();
        // 读取消息类型
        byte msgType = in.readByte();
        // 读取响应状态
        byte status = in.readByte();
        // 读取请求 ID
        long requestId = in.readLong();
        // 获取序列化算法长度
        final int len = in.readInt();
        if (in.readableBytes() < len){
            in.resetReaderIndex();
            return;
        }
        final byte[] bytes = new byte[len];
        in.readBytes(bytes);
        final String serialization = new String(bytes);
        // 读取消息体长度
        int dataLength = in.readInt();
        // 如果可读字节数小于消息体长度，说明还没有接收完整个消息体，回退并返回
        if (in.readableBytes() < dataLength) {
            // 回退标记位置
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        // 读取数据
        in.readBytes(data);

        // 处理消息的类型
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }

        // 构建消息头
        MsgHeader header = new MsgHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setSerializations(bytes);
        header.setSerializationLen(len);
        header.setMsgLen(dataLength);
        // 获取序列化器
        RpcSerialization rpcSerialization = SerializationFactory.get(serialization);
        // 根据消息类型进行处理(如果消息类型过多可以使用策略+工厂模式进行管理)
        switch (msgTypeEnum) {
            // 请求消息
            case REQUEST:
                RpcRequest request = rpcSerialization.deserialize(data, RpcRequest.class);
                if (request != null) {
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
             // 响应消息
            case RESPONSE:
                RpcResponse response = rpcSerialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
        }
    }
}
