package com.nove.version;


import com.nove.version.ribbon.EurekaServerExtractor;

/**
 *  用于获取版本信息
 * @author QIANGLU
 */
public class RedefineVersionAppContext {

    private static RedefineVersionRibbonConnectionPoint defaultConnectionPoint;
    private static EurekaServerExtractor eurekaServerExtractor;
    private static String localIp;

    public static RedefineVersionRibbonConnectionPoint getVersionRibbonConnectionPoint(){
        return defaultConnectionPoint;
    }


    static void setDefaultConnectionPoint(RedefineVersionRibbonConnectionPoint connectionPoint){
        RedefineVersionAppContext.defaultConnectionPoint = connectionPoint;
    }

    public static EurekaServerExtractor getEurekaServerExtractor() {
        return eurekaServerExtractor;
    }

    static void setEurekaServerExtractor(EurekaServerExtractor eurekaServerExtractor) {
        RedefineVersionAppContext.eurekaServerExtractor = eurekaServerExtractor;
    }

    public static String getLocalIp() {
        return localIp;
    }

    static void setLocalIp(String localIp) {
        RedefineVersionAppContext.localIp = localIp;
    }
}
