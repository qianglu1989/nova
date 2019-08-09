package com.nove.version.ribbon.loadbalancer;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.nove.version.RedefineVersionAppContext;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author QIANGLU
 */
public class RedefineVerZoneAvoidanceRule extends ZoneAvoidanceRule {

    protected CompositePredicate predicate;


    public static final String META_DATA_KEY_WEIGHT = "weight";

    private Random random = new Random();

    public RedefineVerZoneAvoidanceRule() {
        super();
        RedefineVerApiVersionPredicate apiVersionPredicate = new RedefineVerApiVersionPredicate(this);
        predicate = CompositePredicate.withPredicates(super.getPredicate(),
                apiVersionPredicate).build();
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        return predicate;
    }


    public Map<String, String> getServerMetadata(String serviceId, Server server) {
        return RedefineVersionAppContext.getEurekaServerExtractor().getServerMetadata(serviceId, server);
    }

    @Override
    public Server choose(Object key) {
        List<Server> serverList = this.getPredicate().getEligibleServers(getLoadBalancer().getAllServers(), key);
        if (CollectionUtils.isEmpty(serverList)) {
            return null;
        }

        List<Server> weightList = new ArrayList<>();
        for (Server server : serverList) {
            Map<String, String> metadata = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();


            String strWeight = metadata.get(META_DATA_KEY_WEIGHT);

            int weight = 10;
            try {
                weight = Integer.parseInt(strWeight);
            } catch (Exception e) {
            }

            if (weight <= 0) {
                continue;
            }

            for (int i = 0; i < weight; i++) {
                weightList.add(server);

            }

        }

        // 权重随机
        int randomWight = this.random.nextInt(weightList.size());


        return weightList.get(randomWight);
    }
}
