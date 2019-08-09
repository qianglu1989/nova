package com.redefine.nove.cache;

import com.alibaba.fastjson.JSON;
import com.redefine.nove.utils.PropertyConfigUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 用于等待读取配置到应用内存进行动态刷新
 * Created by QIANG on 2017/12/14
 *
 * @author QIANG
 */
public class NovePathNodeListener implements PathChildrenCacheListener {

    private static final Logger LOG = LoggerFactory.getLogger(NovePathNodeListener.class);

    /**
     * 分布式配置监听
     * 配置格式为：节点名称与APPNAME一致，里面的信息为多维结构,分为公共参数区域和私有参数区域，此监听动态加载的为私有参数区域（{"private":{"test":"1233333"}}）
     *
     * @param client
     * @param event
     * @throws Exception
     */
    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        String path = ZkConstants.NOVE_CONFIG_BASE + PropertyConfigUtils.getProperty("spring.application.name");

        //存储动态参数
        if (PathChildrenCacheEvent.Type.CHILD_UPDATED.equals(event.getType()) || PathChildrenCacheEvent.Type.CHILD_ADDED.equals(event.getType())) {

            if(!path.equals(event.getData().getPath())){
                return;
            }

            String json = new String(event.getData().getData());
            LOG.info("收到配置中心数据变更操作,data:{}",json);
            Map<String, String> result = JSON.parseObject(json, Map.class);
            PropertyConfigUtils.putAll(result);
        }
    }

}
