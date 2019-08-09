package com.redefine.kafka.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Created by luqiang on 2018/6/25.
 *
 * @author QIANGLU
 */
@Configuration
public class RedefineHiveAutoConfiguration {

    @Resource
    private Environment env;

    @Bean(name = "hiveJdbcDataSource")
    @Qualifier("hiveJdbcDataSource")
    public DataSource dataSource() {

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(env.getProperty("redefine.hive.url"));
        dataSource.setDriverClassName(env.getProperty("redefine.hive.driver"));

        String maxActive = env.getProperty("redefine.hive.maxActive");
        String initialSize = env.getProperty("redefine.hive.initialSize");
        dataSource.setMaxActive(StringUtils.isNotEmpty(maxActive)  ? Integer.valueOf(maxActive) : 100);
        dataSource.setInitialSize(StringUtils.isNotEmpty(initialSize)   ? Integer.valueOf(initialSize) : 10);
        dataSource.setTestWhileIdle(false);
        return dataSource;
    }


    @Bean(name = "hiveJdbcTemplate")
    public JdbcTemplate hiveJdbcTemplate(@Qualifier("hiveJdbcDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
