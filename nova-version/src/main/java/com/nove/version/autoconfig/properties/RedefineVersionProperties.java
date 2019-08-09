package com.nove.version.autoconfig.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author luqiang
 */
@ConfigurationProperties("redefine.multi.version")
public class RedefineVersionProperties {

    private RedefineVerRequest redefineVerRequest = new RedefineVerRequest();

    public RedefineVerRequest getRedefineVerRequest() {
        return redefineVerRequest;
    }

    public void setRedefineVerRequest(RedefineVerRequest redefineVerRequest) {
        this.redefineVerRequest = redefineVerRequest;
    }

    public static class RedefineVerRequest {

        private boolean loadBody = false;

        /**
         * 是否读取并加载请求的body数据
         *
         * @return
         */
        public boolean isLoadBody() {
            return loadBody;
        }

        public void setLoadBody(boolean loadBody) {
            this.loadBody = loadBody;
        }
    }

}
