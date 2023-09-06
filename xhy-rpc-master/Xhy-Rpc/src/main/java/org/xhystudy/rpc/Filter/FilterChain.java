package org.xhystudy.rpc.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 拦截器链
 * @Author: Xhy
 * @gitee: https://gitee.com/XhyQAQ
 * @copyright: B站: https://space.bilibili.com/152686439
 * @CreateTime: 2023-08-03 12:21
 */
public class FilterChain {


    private List<Filter> filters = new ArrayList<>();

    public void addFilter(Filter filter){
        filters.add(filter);
    }


    public void addFilter(List<Object> filters){
        for (Object filter : filters) {
            addFilter((Filter) filter);
        }
    }
    public void doFilter(FilterData data){
        for (Filter filter : filters) {
            filter.doFilter(data);
        }
    }
}
