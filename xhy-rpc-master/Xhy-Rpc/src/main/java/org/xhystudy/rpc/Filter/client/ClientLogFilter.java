package org.xhystudy.rpc.Filter.client;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhystudy.rpc.Filter.ClientBeforeFilter;
import org.xhystudy.rpc.Filter.FilterData;


/**
 * @description: 日志
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-08-03 11:33
 */
public class ClientLogFilter implements ClientBeforeFilter {

    private Logger logger = LoggerFactory.getLogger(ClientLogFilter.class);


    @Override
    public void doFilter(FilterData filterData) {
        logger.info(filterData.toString());
    }
}
