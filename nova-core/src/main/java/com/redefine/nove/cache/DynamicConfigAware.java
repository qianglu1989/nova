package com.redefine.nove.cache;

import com.alibaba.fastjson.JSON;
import com.redefine.nove.utils.PropertyConfigUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 用于读取动态配置
 * Created by QIANG
 *
 * @author QIANG
 */
public class DynamicConfigAware implements EnvironmentAware {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicConfigAware.class);

    private ZkManager zkManager;

    public DynamicConfigAware(ZkManager zkManager) {
        this.zkManager = zkManager;
    }

    @Override
    public void setEnvironment(Environment environment) {
        PropertyConfigUtils.setProperties("server.port", environment.getProperty("server.port"));
        init(environment);
    }

    private void init(Environment environment) {
        String appName = environment.getProperty("spring.application.name");
        CuratorFramework client = zkManager.getZkClient();
        String path = ZkConstants.NOVE_CONFIG_BASE + appName;
        try {
            Stat stat = client.checkExists().forPath(path);
            if (stat == null) {
                LOG.warn("注意：项目{}启动初始化远端配置节点不存在");
                return;
            }
            String data = new String(client.getData().forPath(path));
            if(StringUtils.isEmpty(data)){
                return;
            }
            Map<String, String> result = JSON.parseObject(data, Map.class);
            String pub = result.get("public");

            if(StringUtils.isEmpty(pub)){
                return;
            }
            List<String> pubParam = Arrays.asList(pub.split(","));

            for(String param : pubParam){
                String node  = ZkConstants.NOVE_CONFIG_BASE + "commons" + ZkConstants.NOVE_CONFIG_BASE +param;
                Stat pStat = client.checkExists().forPath(node);
                if (pStat == null) {
                    LOG.warn("注意：项目{}启动初始化远端公共配置节点不存在");
                    continue;
                }
                String pData = new String(client.getData().forPath(node));
                PropertyConfigUtils.putAll(JSON.parseObject(pData,Map.class));
                LOG.info("添加公共参数{}",param);
            }

        } catch (Exception e) {
            LOG.warn("注意：项目{}启动初始化远端配置失败,异常：【{}】", e.getMessage());
        }
    }
}
