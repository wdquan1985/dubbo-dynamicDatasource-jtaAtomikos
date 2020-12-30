package yiyi.example.dubbo.dynamicdatasource.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import yiyi.example.dubbo.common.MultiDataSourceService;
import yiyi.example.dubbo.common.entity.Product;
import yiyi.example.dubbo.common.entity.TestUser;
import yiyi.example.dubbo.dynamicdatasource.annotation.DS;
import yiyi.example.dubbo.dynamicdatasource.constants.DataSourceConstants;
import yiyi.example.dubbo.dynamicdatasource.context.DynamicDataSourceContextHolder;
import yiyi.example.dubbo.dynamicdatasource.mapper.ProductMapper;
import yiyi.example.dubbo.dynamicdatasource.mapper.TestUserMapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 
 * @author bwang018 2020/12/19
 *
 */
@org.apache.dubbo.config.annotation.Service(version = "${helloService.version}")
public class MultiDataSourceServiceImpl implements MultiDataSourceService{
    @Autowired
    private TestUserMapper testUserMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    /**
     * 查询主、从数据库中User表中的所有数据
     * @return
     */
    @Override
//    @Transactional
    public Map<String, Object> getAllUsers(){
    	Map<String, Object> map = new HashMap<String, Object>();
    	//查询从数据库中的user数据，这种情况下无法通过在方法上加注解 @DS来切换数据源，所以直接切换。
    	DynamicDataSourceContextHolder.setContextKey(DataSourceConstants.DS_KEY_SLAVE);
        List<TestUser> testUsers = testUserMapper.selectList(null);
        map.put("slave", testUsers);
        DynamicDataSourceContextHolder.removeContextKey();
        
        //查询主数据库中的user数据，这种情况下无法通过在方法上加注解 @DS来切换数据源，所以直接切换
        DynamicDataSourceContextHolder.setContextKey(DataSourceConstants.DS_KEY_MASTER);
        List<TestUser> testUsers1 = testUserMapper.selectList(null);
        map.put("master", testUsers1);
        DynamicDataSourceContextHolder.removeContextKey();
        return map;
    }

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
    @Override
    @Transactional
    public void insertTwoUsers(boolean throwException){
        //往从数据库中插入数据， 这种情况下无法通过在方法上加注解 @DS来切换数据源，所以直接切换。
        DynamicDataSourceContextHolder.removeContextKey();
        DynamicDataSourceContextHolder.setContextKey(DataSourceConstants.DS_KEY_SLAVE);
    	TestUser testUser = new TestUser();
    	testUser.setName("userSlave");
    	testUser.setPhone("13674890382");
    	testUser.setRecordVersion(110L);
    	testUser.setEmail("wdquan1985@163.com");
    	testUser.setTitle("transcation_test");
        testUserMapper.insert(testUser);
        DynamicDataSourceContextHolder.removeContextKey();
        
        if (throwException) {
        	int a = 1 / 0;
		}
        
        DynamicDataSourceContextHolder.setContextKey(DataSourceConstants.DS_KEY_MASTER);
    	TestUser testUser1 = new TestUser();
    	testUser1.setName("userMaster");
    	testUser1.setPhone("13674890382");
    	testUser1.setRecordVersion(110L);
    	testUser1.setEmail("hellworld@163.com");
    	testUser1.setTitle("transcation_test");
        testUserMapper.insert(testUser1);
        DynamicDataSourceContextHolder.removeContextKey();
        //此处会报错，引发事务回滚
//        int a = 1 / 0;
    }
    
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
    @Override
    @Transactional
    public void insertTwoTables(boolean throwException){
    	//往主数据库中插入数据，这种情况下无法通过在方法上加注解 @DS来切换数据源，所以直接切换。
    	DynamicDataSourceContextHolder.setContextKey(DataSourceConstants.DS_KEY_MASTER);
        Product product = new Product();
        product.setName("productMaster");
        product.setSize("100");
        product.setHeight("200");
        product.setVendor("shunji");
        product.setWeight("150");
        productMapper.insert(product);
        
        if (throwException) {
        	int a = 1 / 0;
		}
        
        //往从数据库中插入数据， 这种情况下无法通过在方法上加注解 @DS来切换数据源，所以直接切换。
        DynamicDataSourceContextHolder.removeContextKey();
        DynamicDataSourceContextHolder.setContextKey(DataSourceConstants.DS_KEY_SLAVE);
    	TestUser testUser = new TestUser();
    	testUser.setName("userSlave");
    	testUser.setPhone("13674890382");
    	testUser.setRecordVersion(110L);
    	testUser.setEmail("hellworld@163.com");
    	testUser.setTitle("transcation_test");
        testUserMapper.insert(testUser);
        DynamicDataSourceContextHolder.removeContextKey();
        //此处会报错，引发事务回滚
//        int a = 1 / 0;
    }
    
    //主数据库中查询User信息
    @Override
    @DS(value = DataSourceConstants.DS_KEY_MASTER)
    @Transactional
    public TestUser getMasterUserById(String id) {
      TestUser testUser = testUserMapper.selectOne(new QueryWrapper<TestUser>().eq("id" , id));      
      if (testUser != null) {
          return testUser;
      } else {
          return null;
      }
   }

    //从数据库中查询User信息
    @Override
    @DS(value = DataSourceConstants.DS_KEY_SLAVE)
    @Transactional
    public TestUser getSlaveUserById(String id) {
      TestUser testUser = testUserMapper.selectOne(new QueryWrapper<TestUser>().eq("id" , id));      
      if (testUser != null) {
          return testUser;
      } else {
          return null;
      }
   }
}
