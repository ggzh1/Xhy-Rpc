package org.xhystudy.rpc.protocol.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * @description: JSON序列化
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-04-30 12:41
 */
public class JsonSerialization implements RpcSerialization {


    private static final ObjectMapper MAPPER;

    static {
        MAPPER = generateMapper(JsonInclude.Include.ALWAYS);
    }

    private static ObjectMapper generateMapper(JsonInclude.Include include) {
        ObjectMapper customMapper = new ObjectMapper();
        customMapper.setSerializationInclusion(include);
        customMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        customMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        customMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return customMapper;
    }

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return obj instanceof String ? ((String) obj).getBytes() : MAPPER.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        if (clz.equals(String.class)) {
            return (T) new String(data, StandardCharsets.UTF_8);
        }
        return MAPPER.readValue(data, clz);
    }
}

