package com.nove.version;

/**
 * 请求连接上下文信息
 * @author QIANGLU
 */
public class ConnectPointContext {

    static final ThreadLocal<ConnectPointContext> CONTEXT_LOCAL = new ThreadLocal<>();


    private RedefineVersionRequest versionRequest;
    private Throwable excption;

    private ConnectPointContext(RedefineVersionRequest versionRequest) {
        this.versionRequest = versionRequest;
    }

    public RedefineVersionRequest getVersionRequest() {
        return versionRequest;
    }

    void setBambooRequest(RedefineVersionRequest versionRequest) {
        this.versionRequest = versionRequest;
    }


    public Throwable getExcption() {
        return excption;
    }

    void setExcption(Throwable excption) {
        this.excption = excption;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private RedefineVersionRequest versionRequest;

        private Builder() {

        }


        public Builder versionRequest(RedefineVersionRequest versionRequest) {
            this.versionRequest = versionRequest;
            return this;
        }

        public ConnectPointContext build() {
            return new ConnectPointContext(versionRequest);
        }
    }


    public static ConnectPointContext getContextLocal() {
        return CONTEXT_LOCAL.get();
    }

    public void remove(){
        CONTEXT_LOCAL.remove();
    }

}
