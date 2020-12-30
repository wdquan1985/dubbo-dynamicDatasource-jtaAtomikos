package yiyi.example.dubbo.dynamicdatasource.vo;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 
 * @author bwang018 2020/12/19
 *
 */
@Configuration
@PropertySource("classpath:config/jdbc.properties")
@ConfigurationProperties(prefix = "spring.datasource.slave")
@Data
public class SlaveDatabaseProp {
	private String jdbcUrl;
	private String username;
	private String password;
	private String type;
	private int minPoolSize;
	private int maxPoolSize;
	private int maxLifetime;
	private int borrowConnectionTimeout;
	private int loginTimeout;
	private int maintenanceInterval;
	private int maxIdleTime;
	private String testQuery;
}
