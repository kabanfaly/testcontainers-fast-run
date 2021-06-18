package com.geekshow.testcontainersfastrun;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories(basePackages ="com.geekshow.testcontainersfastrun.repository")
@Configuration
public class Config {
}
