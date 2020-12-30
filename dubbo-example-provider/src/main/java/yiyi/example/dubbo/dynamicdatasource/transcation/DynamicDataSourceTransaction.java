package yiyi.example.dubbo.dynamicdatasource.transcation;

import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.transaction.Transaction;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

import yiyi.example.dubbo.dynamicdatasource.context.DynamicDataSourceContextHolder;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 * 根据Spring官方介绍，要想自己控制事务，则必须实现Transaction接口，所以我们来创建动态数据源事务实现类DynamicDataSourceTransaction
 */
/**
 * <P>多数据源切换，支持事务</P>
* <P>多数据源事务管理器是:根据数据源的不同类型，动态获取数据库连接，而不是从原来的缓存中(ThreadLocal ???)获取导致数据源没法切换</P>
* @author Bruce 2020/12/19
 */
@Slf4j
public class DynamicDataSourceTransaction implements Transaction{
    private final DataSource dataSource;
    private Connection defaultConnection;
    private String dataBaseName;
    private ConcurrentMap<String, Connection> dynamicConnectionMap;
    private boolean isConnectionTransactional;
    private boolean autoCommit;

    public DynamicDataSourceTransaction(DataSource dataSource) {
        Assert.notNull(dataSource, "No DataSource specified");
        this.dataSource = dataSource;
        this.dynamicConnectionMap = new ConcurrentHashMap<>();
        this.dataBaseName = DynamicDataSourceContextHolder.getContextKey();
    }
    
    /**
     * 开启事务处理方法, 事务获取数据源的Connection.
     */
    @Override
    public Connection getConnection() throws SQLException {
        String dataBase = DynamicDataSourceContextHolder.getContextKey();
        if (dataBase.equals(dataBaseName)) {
            if (defaultConnection != null) {
                return defaultConnection;
            }
            openMainConnection();
            dataBaseName = dataBase;
            return defaultConnection;
        } else {
            if (!dynamicConnectionMap.containsKey(dataBase)) {
                try {
                    Connection conn = dataSource.getConnection();
                    dynamicConnectionMap.put(dataBase, conn);
                } catch (SQLException ex) {
                    throw new CannotGetJdbcConnectionException("Could not get JDBC Connection", ex);
                }
            }
            return dynamicConnectionMap.get(dataBase);
        }
    }
    
    private void openMainConnection() throws SQLException {
        this.defaultConnection = DataSourceUtils.getConnection(this.dataSource);
        this.autoCommit = this.defaultConnection.getAutoCommit();
        this.isConnectionTransactional = DataSourceUtils.isConnectionTransactional(this.defaultConnection, this.dataSource);

        if (log.isDebugEnabled()) {
            log.debug(
                    "JDBC Connection ["
                            + this.defaultConnection
                            + "] will"
                            + (this.isConnectionTransactional ? " " : " not ")
                            + "be managed by Spring");
        }
    }

    /**
     * 提交处理方法
     */
    @Override
    public void commit() throws SQLException {
        if (this.defaultConnection != null && !this.isConnectionTransactional && !this.autoCommit) {
            if (log.isDebugEnabled()) {
                log.debug("Committing JDBC Connection [" + this.defaultConnection + "]");
            }
            this.defaultConnection.commit();
            for (Connection connection : dynamicConnectionMap.values()) {
                connection.commit();
            }
        }
    }
    
    /**
     * 回滚处理方法
     */
    @Override
    public void rollback() throws SQLException {
        if (this.defaultConnection != null && !this.isConnectionTransactional && !this.autoCommit) {
            if (log.isDebugEnabled()) {
                log.debug("Rolling back JDBC Connection [" + this.defaultConnection + "]");
            }
            this.defaultConnection.rollback();
            for (Connection connection : dynamicConnectionMap.values()) {
                connection.rollback();
            }
        }
    }

    /**
     * 关闭处理方法
     */
    @Override
    public void close() {
        DataSourceUtils.releaseConnection(this.defaultConnection, this.dataSource);
        for (Connection connection : dynamicConnectionMap.values()) {
            DataSourceUtils.releaseConnection(connection, this.dataSource);
        }
    }

    @Override
    public Integer getTimeout() {
        return null;
    }

}
