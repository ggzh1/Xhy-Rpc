package org.xhystudy.rpc.utils;

import org.springframework.core.env.Environment;
import org.xhystudy.rpc.annotation.PropertiesField;
import org.xhystudy.rpc.annotation.PropertiesPrefix;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @description:
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-08-09 11:24
 */
public class PropertiesUtils {

    /**
     * 根据对象中的配置匹配配置文件
     * @param o
     * @param environment
     */
    public static void init(Object o, Environment environment){
        final Class<?> aClass = o.getClass();
        // 获取前缀
        final PropertiesPrefix prefixAnnotation = aClass.getAnnotation(PropertiesPrefix.class);
        if (prefixAnnotation == null){
            throw new NullPointerException(aClass + " @PropertiesPrefix 不存在");
        }
        String prefix = prefixAnnotation.value();
        // 前缀参数矫正
        if (!prefix.contains(".")){
            prefix += ".";
        }
        // 遍历对象中的字段
        for (Field field : aClass.getDeclaredFields()) {
            final PropertiesField fieldAnnotation = field.getAnnotation(PropertiesField.class);
            if (fieldAnnotation == null) continue;;
            String fieldValue = fieldAnnotation.value();
            if(fieldValue == null || fieldValue.equals("")){
                fieldValue = convertToHyphenCase(field.getName());
            }
            try {
                field.setAccessible(true);
                final Class<?> type = field.getType();
                final Object value = PropertyUtil.handle(environment, prefix + fieldValue, type);
                if(value == null) continue;
                field.set(o, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }
    }



    public static String convertToHyphenCase(String input) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                output.append('-');
                output.append(Character.toLowerCase(c));
            } else {
                output.append(c);
            }
        }

        return output.toString();
    }
}
