package com.nove.version.ribbon.loadbalancer;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.nove.version.RedefineVersionRequestContext;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author QIANGLU
 * 过滤规则
 */
public class RedefineVerApiVersionPredicate extends AbstractServerPredicate {


    public RedefineVerApiVersionPredicate(RedefineVerZoneAvoidanceRule rule) {
        super(rule);
    }

    @Override
    public boolean apply(PredicateKey input) {
        RedefineVerLoadBalancerKey loadBalancerKey = getBambooLoadBalancerKey(input);
        if (loadBalancerKey != null && !StringUtils.isEmpty(loadBalancerKey.getApiVersion())) {
            Map<String, String> serverMetadata = ((RedefineVerZoneAvoidanceRule) this.rule)
                    .getServerMetadata(loadBalancerKey.getServiceId(), input.getServer());
            String versions = serverMetadata.get("versions");
            return matchVersion(versions, loadBalancerKey.getApiVersion());
        }
        return true;
    }

    private RedefineVerLoadBalancerKey getBambooLoadBalancerKey(PredicateKey input) {

        if(RedefineVersionRequestContext.currentRequestCentxt()!=null){
            RedefineVersionRequestContext versionRequestContext = RedefineVersionRequestContext.currentRequestCentxt();
            String apiVersion = versionRequestContext.getApiVersion();
            if(!StringUtils.isEmpty(apiVersion)){
                return RedefineVerLoadBalancerKey.builder().apiVersion(apiVersion)
                        .serviceId(versionRequestContext.getServiceId()).build();
            }
        }
        return null;
    }

    /**
     * 匹配api version
     * @param serverVersions
     * @param apiVersion
     * @return
     */
    private boolean matchVersion(String serverVersions, String apiVersion) {
        if (StringUtils.isEmpty(serverVersions)) {
            return false;
        }
        String[] versions = StringUtils.split(serverVersions, ",");
        return ArrayUtils.contains(versions, apiVersion);
    }
}
