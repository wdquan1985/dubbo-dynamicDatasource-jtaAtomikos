package yiyi.example.dubbo.dynamicdatasource.transcation;

import javax.sql.DataSource;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

/**
 * 
 * @author bwang018 2020/12/19
 * 重写动态数据源事务管理工厂
 *
 */
public class DynamicDataSourceTransactionFactory extends SpringManagedTransactionFactory{
    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new DynamicDataSourceTransaction(dataSource);
    }
}
