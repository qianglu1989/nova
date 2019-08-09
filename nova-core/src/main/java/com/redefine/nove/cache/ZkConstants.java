package com.redefine.nove.cache;

/**
 * Created by QIANG on 2017/12/14
 * @author QIANG
 */
public class ZkConstants {

    /**
     * 根节点
     */
    public static final String NOVE_CONFIG_BASE = "/";

    /**
     * ZK动态配置根节点
     */
    public static final String NOVE_CONFIG_CENTER = "/noveConfigCenter";

    /**
     * ZK动态配置公共参数节点
     */
    public static final String NOVE_CONFIG_COMMONS = "/commons";

    /**
     * ZK动态配置命名空间
     */
    public static final String NOVE_CONFIG_NAMESPACE = "noveConfigCenter";


    /**
     * ZK动态配置参数
     */
    public static final String NOVE_CONFIG_PUBLIC = "public";

    /**
     * ZK动态配置REDIS中的KEY
     */
    public static final String NOVE_CONFIG_DISCONFIG= "nove:disconfig";
}
