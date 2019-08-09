package com.redefine.nove.cache;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by QIANG on 2017/12/14
 *
 * @author QIANG
 */
public class NoveZkManager implements ZkManager {

    private static final Logger LOG = LoggerFactory.getLogger(NoveZkManager.class);


    private CuratorFramework client;

    private PathChildrenCache cache;

    private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    private List<PathChildrenCacheListener> listeners = Collections.synchronizedList(new ArrayList<PathChildrenCacheListener>());

    public NoveZkManager(String hosts) {
       this(hosts,null);
    }


    public NoveZkManager(String hosts,PathChildrenCacheListener listener) {
        if(listener != null ){
            listeners.add(listener);
        }
        client = CuratorFrameworkFactory.builder().connectString(hosts).retryPolicy(retryPolicy).namespace(ZkConstants.NOVE_CONFIG_NAMESPACE).build();
        init();
    }

    public NoveZkManager(String hosts, int sessionTimeout, int connectionTimeout) {

        client = CuratorFrameworkFactory.builder()
                .connectString(hosts)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectionTimeout)
                .retryPolicy(retryPolicy)
                .namespace(ZkConstants.NOVE_CONFIG_NAMESPACE)
                .build();
        init();
    }


    private void init() {

        client.start();
        try {
            Stat stat = client.checkExists().forPath(ZkConstants.NOVE_CONFIG_COMMONS);
            if (stat == null) {
                client.create().forPath(ZkConstants.NOVE_CONFIG_COMMONS,"".getBytes());
            }

            //添加监听，用于监听所有子节点
            cache = new PathChildrenCache(client, ZkConstants.NOVE_CONFIG_BASE, true);
            cache.start(PathChildrenCache.StartMode.NORMAL);

            for (PathChildrenCacheListener listener : listeners){
                cache.getListenable().addListener(listener);
            }

        } catch (Exception e) {
            LOG.warn("启动应用配置在初始化时出现致命错误：{}", e.getMessage());
            client.close();
        }
    }

    @Override
    public CuratorFramework getZkClient() {
        return client;
    }

    @Override
    public void addConnectionListener(PathChildrenCacheListener listener) {
        listeners.add(listener);
        cache.getListenable().addListener(listener);
    }
}
