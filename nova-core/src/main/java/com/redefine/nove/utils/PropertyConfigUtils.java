package com.redefine.nove.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.*;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.*;

/**
 * 扩展spring配置读取方式，在容器启动时优先加载配置文件到内存，不需要单独读取配置
 *
 * @author QIANG
 * @data 2016年7月4日上午10:05:00
 */
public class PropertyConfigUtils extends PropertyResourceConfigurer {

    private static Logger log = LoggerFactory.getLogger(PropertyConfigUtils.class);


    public static Set<String> standardSources = new HashSet<>(
            Arrays.asList(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME,
                    StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                    StandardServletEnvironment.JNDI_PROPERTY_SOURCE_NAME,
                    StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME,
                    StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME));

    private ConfigurableApplicationContext context;

    public PropertyConfigUtils(ConfigurableApplicationContext context) {
        this.context = context;
    }

    private static Map<String, Object> ctxPropertiesMap = new HashMap<>();

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props)
            throws BeansException {

        log.debug("PropertyConfigure execute ........");

        Map<String, Object> datas = extract(this.context.getEnvironment().getPropertySources());
        ctxPropertiesMap.putAll(datas);

        log.info("PropertyConfigure load finish,size:{}", ctxPropertiesMap.size());
    }

    public static Object getPropertyObj(String name) {

        return ctxPropertiesMap.get(name);
    }

    public static String getProperty(String name) {

        Object obj = getPropertyObj(name);
        if (obj != null) {
            return (String) obj;
        }

        return "";
    }

    public static Object getPropertyObj(String name, String value) {

        Object v = ctxPropertiesMap.get(name);
        if (v != null && StringUtils.isNotEmpty((String) v)) {
            return v;
        }
        return value;
    }

    public static String getProperty(String name, String value) {

        return (String) getPropertyObj(name, value);
    }

    public static void setProperties(String key, String val) {
        ctxPropertiesMap.put(key, val);
    }


    public static void putAll(Map map) {
        if (map != null && map.size() > 0) {
            ctxPropertiesMap.putAll(map);
        }
    }

    public static Map<String, Object> getAll() {
        return ctxPropertiesMap;
    }

    public static boolean remove(Object key, Object val) {
        return ctxPropertiesMap.remove(key, val);
    }

    public static void remove(Object key) {
        ctxPropertiesMap.remove(key);
    }


    public static Map<String, Object> extract(MutablePropertySources propertySources) {
        Map<String, Object> result = new HashMap<>(16);
        List<PropertySource<?>> sources = new ArrayList<PropertySource<?>>();
        for (PropertySource<?> source : propertySources) {
            sources.add(0, source);
        }
        for (PropertySource<?> source : sources) {
            if (!standardSources.contains(source.getName())) {
                extract(source, result);
            }
        }
        return result;
    }

    public static void extract(PropertySource<?> parent, Map<String, Object> result) {
        if (parent instanceof CompositePropertySource) {
            try {
                List<PropertySource<?>> sources = new ArrayList<PropertySource<?>>();
                for (PropertySource<?> source : ((CompositePropertySource) parent)
                        .getPropertySources()) {
                    sources.add(0, source);
                }
                for (PropertySource<?> source : sources) {
                    extract(source, result);
                }
            } catch (Exception e) {
                return;
            }
        } else if (parent instanceof EnumerablePropertySource) {
            for (String key : ((EnumerablePropertySource<?>) parent).getPropertyNames()) {
                result.put(key, parent.getProperty(key));
                log.debug("PropertyConfigure load K[{}] V[{}]", key, parent.getProperty(key));
            }
        }
    }
}
