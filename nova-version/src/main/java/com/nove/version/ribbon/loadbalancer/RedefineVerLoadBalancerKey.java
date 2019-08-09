package com.nove.version.ribbon.loadbalancer;

/**
 * @author QIANGLU
 */
public class RedefineVerLoadBalancerKey {


    private String serviceId;
    private String apiVersion;


    private RedefineVerLoadBalancerKey() {

    }


    public String getApiVersion() {
        return apiVersion;
    }

    public String getServiceId() {
        return serviceId;
    }

    private void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {

        private RedefineVerLoadBalancerKey build = new RedefineVerLoadBalancerKey();

        public Builder apiVersion(String apiVersion) {
            build.apiVersion = apiVersion;
            return this;
        }

        public Builder serviceId(String serviceId) {
            build.serviceId = serviceId;
            return this;
        }

        public RedefineVerLoadBalancerKey build() {
            return build;
        }

    }
}
