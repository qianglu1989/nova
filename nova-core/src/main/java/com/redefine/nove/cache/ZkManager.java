package com.redefine.nove.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

/**
 * Created by QIANG on 2017/12/14
 * @author QIANG
 */
public interface ZkManager {

    /**
     * 获取链接
     * @return
     */
    public CuratorFramework getZkClient();

    /**
     * 添加监听
     * @param listener
     */
    void addConnectionListener(PathChildrenCacheListener listener);

}
