package yiyi.example.dubbo.dynamicdatasource.config;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import yiyi.example.dubbo.dynamicdatasource.constants.DataSourceConstants;
import yiyi.example.dubbo.dynamicdatasource.transcation.DynamicDataSourceTransactionFactory;
import yiyi.example.dubbo.dynamicdatasource.vo.MasterDatabaseProp;
import yiyi.example.dubbo.dynamicdatasource.vo.SlaveDatabaseProp;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.mysql.cj.jdbc.MysqlXADataSource;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源配置
 *
 * @author bwang018 2020/12/19
 * XA或者JTA这种实现是强一致性的体现, 按照互联网的实际场景都是最终一致性，柔性事务. CAP
 * Atomikos是JTA标准中TM的一个实现，还有其他的实现：
 * (1).Java Open Transaction Manager (JOTM)
 * (2).JBoss TS Bitronix Transaction Manager (BTM)
 * (3).Narayana
 * 
 * NOTE: atomikos本身支持使用两段提交协议在一个项目中(注意，是一个项目中)，管理多个数据库事务，如mysql的事务，和符合JMS规范的多个消息事务，
 *       如activemq的事务。但是现在因为微服务框架的流行（分布式）使得多个数据库的事务不在一个项目内，使得一个使用微服务的业务操作可能无法达到一致性。atomikos本身不支持多个微服务项目间的事务管理.
 **/
// 添加此配置，否则 报`The dependencies of some of the beans in the application context form a cycle`
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@EnableTransactionManagement
@Configuration
@PropertySource("classpath:config/jdbc.properties")
@MapperScan(basePackages = "yiyi.example.dubbo.dynamicdatasource.mapper")
public class DynamicDataSourceConfig {

    /**
     * mybatis-plus的分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
    
    // mybatis-plus的乐观锁插件
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }
    
	//第一种获取配置参数的方式
	@Autowired
	MasterDatabaseProp masterDatabaseProp;
	@Autowired
	SlaveDatabaseProp slaveDatabaseProp;
	
	//第二种获取配置参数的方式
//    @Bean(name = "masterDataSourceProperties")
//    @Qualifier("masterDataSourceProperties")
//    @ConfigurationProperties(prefix = "spring.datasource.master")
//    public DataSourceProperties masterDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//    @Bean(name = "slaveDataSourceProperties")
//    @Qualifier("slaveDataSourceProperties")
//    @ConfigurationProperties(prefix = "spring.datasource.slave")
//    public DataSourceProperties slaveDataSourceProperties() {
//        return new DataSourceProperties();
//    }
	
    @Bean(DataSourceConstants.DS_KEY_MASTER)
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
//        return DataSourceBuilder.create().build();
        MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
        mysqlXaDataSource.setUrl(masterDatabaseProp.getJdbcUrl());
        try {
			mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        mysqlXaDataSource.setPassword(masterDatabaseProp.getPassword());
        mysqlXaDataSource.setUser(masterDatabaseProp.getUsername());
        try {
			mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        // 数据源唯一标识
        xaDataSource.setUniqueResourceName("masterDataSourcejta");
        // Mysql分布式数据源(连接池)实现类 -> XADataSource实现类，使用com.mysql.cj.jdbc.MysqlXADataSource
        xaDataSource.setXaDataSourceClassName(masterDatabaseProp.getType());
        // 最小连接数，默认1
        xaDataSource.setMinPoolSize(masterDatabaseProp.getMinPoolSize());
        // 最大连接数，默认1
        xaDataSource.setMaxPoolSize(masterDatabaseProp.getMaxPoolSize());
        // 设置连接在池中被自动销毁之前保留的最大秒数。 可选，默认为0（无限制）。
        xaDataSource.setMaxLifetime(masterDatabaseProp.getMaxLifetime());
        xaDataSource.setBorrowConnectionTimeout(masterDatabaseProp.getBorrowConnectionTimeout());
        try {
			xaDataSource.setLoginTimeout(masterDatabaseProp.getLoginTimeout());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        xaDataSource.setMaintenanceInterval(masterDatabaseProp.getMaintenanceInterval());
        xaDataSource.setMaxIdleTime(masterDatabaseProp.getMaxIdleTime());
        // 返回连接前用于测试连接的SQL查询
        xaDataSource.setTestQuery(masterDatabaseProp.getTestQuery());
        xaDataSource.setXaDataSource(mysqlXaDataSource);
        return xaDataSource;
    }

    @Bean(DataSourceConstants.DS_KEY_SLAVE)
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource slaveDataSource() {
        //JTA标准(XA规范)中的两个角色中的    参与者(participants): 资源管理器(RM)
        MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
        mysqlXaDataSource.setUrl(slaveDatabaseProp.getJdbcUrl());
        try {
			mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        mysqlXaDataSource.setPassword(slaveDatabaseProp.getPassword());
        mysqlXaDataSource.setUser(slaveDatabaseProp.getUsername());
        try {
			mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        //JTA标准(XA规范)中的两个角色中的   协调者(Coordinater): 事务管理器(TM)
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        // 数据源唯一标识
        xaDataSource.setUniqueResourceName("slaveDataSourcejta");
        //Mysql分布式数据源(连接池)实现类 ->  XADataSource实现类，使用com.mysql.cj.jdbc.MysqlXADataSource
        xaDataSource.setXaDataSourceClassName(slaveDatabaseProp.getType());
        xaDataSource.setMinPoolSize(slaveDatabaseProp.getMinPoolSize());
        xaDataSource.setMaxPoolSize(slaveDatabaseProp.getMaxPoolSize());
        xaDataSource.setMaxLifetime(slaveDatabaseProp.getMaxLifetime());
        xaDataSource.setBorrowConnectionTimeout(slaveDatabaseProp.getBorrowConnectionTimeout());
        try {
			xaDataSource.setLoginTimeout(slaveDatabaseProp.getLoginTimeout());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        xaDataSource.setMaintenanceInterval(slaveDatabaseProp.getMaintenanceInterval());
        xaDataSource.setMaxIdleTime(slaveDatabaseProp.getMaxIdleTime());
        // 返回连接前用于测试连接的SQL查询
        xaDataSource.setTestQuery(slaveDatabaseProp.getTestQuery());
        xaDataSource.setXaDataSource(mysqlXaDataSource);
        return xaDataSource;
    }

    @Bean(name = "dynamicDataSource")
    @Primary
    public DataSource dynamicDataSource(
            @Qualifier(DataSourceConstants.DS_KEY_MASTER) DataSource master,
            @Qualifier(DataSourceConstants.DS_KEY_SLAVE) DataSource slave
    		) {
        Map<Object, Object> dataSourceMap = new HashMap<>(2);
        dataSourceMap.put(DataSourceConstants.DS_KEY_MASTER, master);
        dataSourceMap.put(DataSourceConstants.DS_KEY_SLAVE, slave);
        //设置动态数据源
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.setDefaultTargetDataSource(master);

        return dynamicDataSource;
    }
    
    //这是设置mybatis 的 sqlSessionFactory, mybatis也使用session去访问数据库，跟hibernate应该是一样的。
    //session需要用到Datasource的connection， 所以配置里设置了上面的动态数据源 dynamicDataSource。
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
    		@Qualifier("dynamicDataSource") DataSource dataSource
    		) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        //设置动态数据源
        sqlSessionFactory.setDataSource(dataSource);
        //设置动态数据源事务管理
        sqlSessionFactory.setTransactionFactory(new DynamicDataSourceTransactionFactory());

        //配置mybatis-plus相关的配置
        //非常重要，由于我们在这里配置了Mybatis, 所以在application.propertis文件中的mybatis-plus***配置，失效
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        //数据库字段中下划线，转化为model类中属性的驼峰命名,例如 user_order_num 转为 userOrderNum
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        //非常重要--可以自定义ID生成策略。
//        GlobalConfig globalConfig = new GlobalConfig();
//        globalConfig.setIdentifierGenerator(new IdentifierGenerator() {
//			
//			@Override
//			public Number nextId(Object entity) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		  });
//        configuration.setGlobalConfig(globalConfig);
        sqlSessionFactory.setConfiguration(configuration);
        //添加插件
        sqlSessionFactory.setPlugins(new Interceptor[]{
                paginationInterceptor()
        });
        return sqlSessionFactory.getObject();
    }
    
    @Bean(name = "sqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    
    /** 分布式事务管理器
     * 多数据源操作发生异常时，让多数据源的事务进行同步回滚
     * Create by bruce
     */
    @Bean(name = "userTransaction")
    public UserTransaction userTransaction() throws Throwable {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(10000);
        return userTransactionImp;
    }
    @Bean(name = "userTransactionManager", initMethod = "init", destroyMethod = "close")
    public TransactionManager userTransactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(true);
        return userTransactionManager;
    }
    // 默认事务管理器
    //定义个独立的事务管理器，spring会为你管理的。(很遗憾没有研究一下spring是如何接手事务管理的)
    @Bean(name = "transactionManager")
    @DependsOn({"userTransaction", "userTransactionManager"})
    public PlatformTransactionManager transactionManager() throws Throwable {
        UserTransaction userTransaction = userTransaction();
        TransactionManager userTransactionManager = userTransactionManager();
        //JTA
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransaction, userTransactionManager);
        jtaTransactionManager.setAllowCustomIsolationLevels(true);
        return jtaTransactionManager;
    }

}
