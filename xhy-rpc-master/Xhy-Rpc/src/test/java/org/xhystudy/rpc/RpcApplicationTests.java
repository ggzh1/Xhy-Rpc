package org.xhystudy.rpc;


import org.xhystudy.rpc.Filter.FilterData;
import org.xhystudy.rpc.Filter.ServiceBeforeFilter;

import java.util.ServiceLoader;

class RpcApplicationTests {

    public static void main(String[] args) {
        final ServiceLoader<ServiceLoader> load = ServiceLoader.load(ServiceLoader.class);
        for (ServiceLoader serviceLoader : load) {

        }
    }

}
