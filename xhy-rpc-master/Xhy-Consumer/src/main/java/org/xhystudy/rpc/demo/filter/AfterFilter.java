package org.xhystudy.rpc.demo.filter;

import org.xhystudy.rpc.Filter.ClientAfterFilter;
import org.xhystudy.rpc.Filter.FilterData;

/**
 * @description:
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-08-03 15:33
 */
public class AfterFilter implements ClientAfterFilter {

    @Override
    public void doFilter(FilterData filterData) {
        System.out.println("客户端后置处理器启动咯");
    }
}
