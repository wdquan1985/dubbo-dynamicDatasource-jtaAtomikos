# dubbo-dynamicDatasource-jtaAtomikos
This is a simple demo for springboot, dubbo and JTA atomikos.
## 1.Install zookeeper as the registration center for dubbo.
You can download one from zookeeper official website. Start one locally, it's running at *127.0.0.1:2181*.

## 2.Compile this project.
mvn clean package -Dmaven.test.skip=true

## 3.Import this maven project to eclipse.
There are three modules in this project.
+ dubbo-example-common - define the common api
+ dubbo-example-provider - implement the common api and serve as the dubbo service provider.
+ dubbo-example-consumer - consume the dubbo service.  
Has configured zookeeper address by setting the property *dubbo.registry.address* in application.yml of both provider and consumer, please make sure *dubbo.registry.address* matches the real zookeeper address.

## 4.Initialize the database
Create two mysql databases multi_datasource_test1(master) and multi_datasource_test2(slave), then execute the sql file tables-master.sql for multi_datasource_test1, and tables-slave.sql for multi_datasource_test2.  
We use mybatis-plus as the data persistence layer framework, **user can change the primary key generation policy**, for example, @TableId(type=IdType.AUTO), @TableId(value = "id", type=IdType.ID_WORKER) and so on, if you want to test different policy, please do the **"TRUNCATE TABLE 'table name'"** operation before your change.

## 5.API
Set the confumer access port as **9092**  
(1).The following api is used to test **distributed transaction**
+ http://localhost:9092/user/listalldata - Get all the datas from the test_user table of both the master and slave databases.
+ http://localhost:9092/user/insertTwoTables?rollback=false - Insert data to the test_product table of the master databse, and the test_user table of the slave database, it can test the **distributed transaction** function for mutli datasource.
+ http://localhost:9092/user/insertTwoTables?rollback=true - Insert data to the test_product table of the master databse, and the test_user table of the slave database, but it will raise an exception to call a rollback, it can test the **distributed transaction** rollback function for mutli datasource.
+ http://localhost:9092/user/insertTwoUsers?rollback=false - Insert data to the test_user table of both the master and slave databse, also it can test the **distributed transaction** function for mutli datasource.
+ http://localhost:9092/user/insertTwoUsers?rollback=true - Insert data to the test_user table of both the master and slave databse, it will raise an exception to call a rollback, also it can test the **distributed transaction** rollback function for mutli datasource. getmasteruser/{id}  

(2).The following api is used to test the **dubbo** function.
+ http://localhost:9092/dubbotest/helloinfo - Used to test the dubbo functions, such as timeout and retries combination test, load balance test and so on.
**timeout and retries combination test**
```
@Service(version = "${helloService.version}", retries=5, timeout=1000) //define the timeout 1000ms
public class HelloServiceImpl implements HelloService {

    private static final AtomicLong counter = new AtomicLong();
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public Hello hello(String name) {
    	logger.info("invoke the dubbo provider 1");
    	try {
			Thread.sleep(2000); //sleep 2000ms, it will cause timeout exception.
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new Hello(counter.getAndIncrement(), "Hello3 " + name + "!");
    }
}
```
**load balance test**
Change the logger code for every provider instance.
logger.info("invoke the dubbo provider 1"), then start one new provider instance.
logger.info("invoke the dubbo provider 2"), then start one new provider instance.
logger.info("invoke the dubbo provider 3"), then start one new provider instance.

(3).The following two api is used to test **dynamic datasource switching**.
+ http://localhost:9092/user/insertUserInfoToMultiDatasource?username= - Insert user info to master and slave datasource. 
+ http://localhost:9092/user/getmasteruser/{id} - Get one entry from the test_user table.  
+ http://localhost:9092/user/getslaveuser/{id} - Get one entry from the test_user table.





