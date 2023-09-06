# Xhy-Rpc

#### 介绍
Xhy-Rpc
供于学习使用，注释全，视频全
[点我教学视频]()

以SPI为基础架构搭建高拓展高可用的RPC。

模块有：
注册中心
代理层
路由层
容错层
协议层
拦截器层
SPI
业务线程池


#### 软件架构
SpringBoot + Netty



#### 使用说明

1.  将Xhy-Rpc 进行install，再将interface进行install
2.  在consumer和provider 中进行配置注册中心相关配置
3.  启动consumer和provider1/2 模块，进行http请求进行测试就行了
4.  如需进行拓展，参考consumer中的SPI机制即可





