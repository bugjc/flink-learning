package com.bugjc.flink.connector.jdbc.test;

import com.alibaba.fastjson.JSON;
import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.connector.jdbc.DataSourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
class DataSourceConfigTest {
    /**
     * 构建环境配置文件对象
     */
    private static EnvironmentConfig environmentConfig;

    @BeforeAll
    static void init() {
        try {
            environmentConfig = new EnvironmentConfig(new String[]{});
        } catch (Exception exception) {
            log.info("{}", exception.getMessage());
            log.error("初始化环境配置失败！");
        }
    }

    @Test
    void getDataSourceConfig() {
        DataSourceConfig dataSourceConfig = environmentConfig.getComponent(DataSourceConfig.class);
        log.info("getDataSourceConfigFactory：{}", dataSourceConfig.getDataSourceConfigFactory());
        String dataSourceConfigJson = JSON.toJSONString(dataSourceConfig);
        log.info("DataSource 配置信息：{}", dataSourceConfigJson);
        dataSourceConfig = JSON.parseObject(dataSourceConfigJson, DataSourceConfig.class);
        dataSourceConfig.init();
        log.info("getDataSourceConfigFactory：{}", dataSourceConfig.getDataSourceConfigFactory());
    }
}