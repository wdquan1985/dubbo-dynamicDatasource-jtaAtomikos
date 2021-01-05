package yiyi.example.dubbo.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import yiyi.example.dubbo.common.entity.Product;
import yiyi.example.dubbo.common.entity.TestUser;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 
 * @author bwang018 2020/12/19
 *
 */
@Service
public interface MultiDataSourceService {

    
    /**
     * 查询主、从数据库中User表中的所有数据
     * @return
     */
    public Map<String, Object> getAllUsers();
    
    /**
     * 向主、从数据库中两个不同的表中(User和Product表)插入数据
     * @return
     */
    /**
     * 加上@Transactional，为什么无法切换数据源？
     * 因为spring的事务管理使用了aop代理，在方法开始前，已经将当前数据源绑定在了线程中，所以无论怎样切换，使用的都是同一个数据源。
     * spring的事务管理将数据源进行了线程绑定
     * @param throwException
     */
    public void insertTwoTables(boolean throwException);
    
    /**
     * 向主、从数据库中的User表插入数据
     * @return
     */
    /**
     * 加上@Transactional，为什么无法切换数据源？
     * 因为spring的事务管理使用了aop代理，在方法开始前，已经将当前数据源绑定在了线程中，所以无论怎样切换，使用的都是同一个数据源。
     * spring的事务管理将数据源进行了线程绑定
     * @param throwException
     */
    public void insertTwoUsers(boolean throwException);
    
    /**
     * 主数据库中查询User信息
     * @param id
     * @return
     */
    public TestUser getMasterUserById(String id);
    
    /**
     * 从数据库中查询User信息
     * @param id
     * @return
     */
    public TestUser getSlaveUserById(String id);
    
    //主数据库中插入User信息
    public void insertMasterUser(String name);
    
    //从数据库中插入User信息
    public void insertSlaveUser(String name);
}
