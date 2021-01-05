package yiyi.example.dubbo.consumer;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import yiyi.example.dubbo.common.MultiDataSourceService;
import yiyi.example.dubbo.common.ResponseResult;
import yiyi.example.dubbo.common.entity.TestUser;

import java.util.Map;

/**
 * 用户 Controller
 *
 * @author mason
 * @date 2020-01-08
 */
@RestController
@RequestMapping("/user")
public class TestUserController {
    @Reference(version = "${helloService.version}")
    private MultiDataSourceService multiDataSourceService;

    /**
     * 查询主、从数据库中User表中的所有数据
     */
    @GetMapping("/listalldata")
    public Object listAll() {
        Map<String, Object> result = multiDataSourceService.getAllUsers() ;
        //返回数据
        return ResponseResult.success(result);
    }
    
    /**
     * 向多个数据源中插入user信息。
     * @return
     */
    @GetMapping("/insertUserInfoToMultiDatasource")
    public void insertUserInfoToMultiDatasource(@RequestParam(value="username", required=true) String username) {
    	multiDataSourceService.insertMasterUser(username);
    	multiDataSourceService.insertSlaveUser(username);
    }
    
    /**
     * 向主、从数据库中两个不同的表中(User和Product表)插入数据
     */
    @GetMapping("/insertTwoTables")
    public void insertData(@RequestParam(value="rollback", required=true) boolean rollback) {
    	multiDataSourceService.insertTwoTables(rollback);
    }
    
    /**
     * 向主、从数据库中的User表中插入数据
     */
    @GetMapping("/insertTwoUsers")
    public void insertDatatoUsers(@RequestParam(value="rollback", required=true) boolean rollback) {
    	multiDataSourceService.insertTwoUsers(rollback);
    }
    
    /**
     * 主数据库中查询User信息
     */
    @GetMapping("/getmasteruser/{id}")
    public Object getMasterUserById(@PathVariable(value = "id") String id) {
    	TestUser testUser = multiDataSourceService.getMasterUserById(id);
    	return ResponseResult.success(testUser);
    }
    
    /**
     * 从数据库中查询User信息
     */
    @GetMapping("/getslaveuser/{id}")
    public Object getSlaveUserById(@PathVariable(value = "id") String id) {
    	TestUser testUser = multiDataSourceService.getSlaveUserById(id);
    	return ResponseResult.success(testUser);
    }
}
