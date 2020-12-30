package yiyi.example.dubbo.dynamicdatasource.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import yiyi.example.dubbo.dynamicdatasource.context.DynamicDataSourceContextHolder;

/**
 * 动态数据源
 *
 * @author bwang018 2020/12/19
 * Mybatis执行一条SQL语句的时候，需要先获取一个Connection。这时候，就交由Spring管理器到DataSource中获取连接。
 * Spring中有个具有路由功能的DataSource，它可以通过查找键调用不同的数据源，这就是AbstractRoutingDataSource。
 **/
public class DynamicDataSource extends AbstractRoutingDataSource {
	//当具体执行某个数据库操作时，根据这个方法返回的数据库的编号，以决定使用哪个DataSource.
    @Override
    protected Object determineCurrentLookupKey() {
    	//从ThreadLocal中取得当前线程的value（数据库编号）
        return DynamicDataSourceContextHolder.getContextKey();
    }
}
