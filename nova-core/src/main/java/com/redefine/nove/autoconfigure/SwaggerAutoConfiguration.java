package com.redefine.nove.autoconfigure;

import com.redefine.nove.utils.PropertyConfigUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;

/**
 * add
 * Created by luqiang on 2018/3/13.
 */

/**
 * @author luqiang
 * @Configuration
 * @EnableSwagger2
 * @AutoConfigureAfter(EurekaClientAutoConfiguration.class)
 * @ConditionalOnProperty(prefix = "redefine.swagger",name = "open",havingValue = "true")
 */

public class SwaggerAutoConfiguration  {

    private final static Logger LOGGER = LoggerFactory.getLogger(SwaggerAutoConfiguration.class);
    private EurekaInstanceConfigBean eurekaInstanceConfigBean;

    private ServerProperties serverProperties;

    private static String REQUEST_PREFIX = "http://";

    private static String REQUEST_TAIL= "/swagger-ui.html";

    private static String DASHBOARD_TAIL= "/hystrix";



    public SwaggerAutoConfiguration(EurekaInstanceConfigBean eurekaInstanceConfigBean,ServerProperties serverProperties){

        this.eurekaInstanceConfigBean = eurekaInstanceConfigBean;
        this.serverProperties =serverProperties;
        swaggerPathConfig();
    }





    private void swaggerPathConfig(){
        String swaggerPath = PropertyConfigUtils.getProperty("redefine.swagger.path");
        if(StringUtils.isEmpty(swaggerPath)){
            swaggerPath = this.eurekaInstanceConfigBean.getIpAddress()+":"+this.serverProperties.getPort();
        }
        String path = REQUEST_PREFIX + swaggerPath + REQUEST_TAIL;

        LOGGER.info("add swagger page ulr :{}",path);
        this.eurekaInstanceConfigBean.setStatusPageUrl(path);
    }




}
