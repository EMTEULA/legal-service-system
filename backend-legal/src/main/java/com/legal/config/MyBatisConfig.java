package com.legal.config;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisConfig {
    @Bean
    public ConfigurationCustomizer mapWrapperCustomizer() {
        return configuration ->
                configuration.setObjectWrapperFactory(new CamelCaseMapWrapperFactory());
    }
}
