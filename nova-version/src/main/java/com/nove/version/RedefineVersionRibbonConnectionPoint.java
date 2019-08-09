package com.nove.version;


/**
 * 个接口是负责将redefine-version 跟ribbon连接起来的，将请求的信息， 以及根据业务需要添加的一些路由信息，和获取请求接口的目标版本，
 * 还有触发执行LoadBanceRequestTrigger等，都是由该接口的实现类DefaultRibbonConnectionPoint负责实现。
 * @author luqiang
 */
public interface RedefineVersionRibbonConnectionPoint {


    /**
     * ab
     * @param connectPointContext
     */
    void executeConnectPoint(ConnectPointContext connectPointContext);


    /**
     * ab
     */
    void shutdownconnectPoint();


}
