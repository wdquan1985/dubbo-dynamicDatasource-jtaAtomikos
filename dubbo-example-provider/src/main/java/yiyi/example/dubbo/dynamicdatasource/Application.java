package yiyi.example.dubbo.dynamicdatasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring boot application.
 * 如何测试负载均衡？dubbo已经实现了负载均衡，默认采用随机策略。
 * (1).在application.yml配置文件中，修改dubbo协议的端口（为了便于观察，同时修改HelloServiceImpl.java文件中的打印语句），启动一个实例
 * (2).再次修改，然后再次启动一个新的实例，想启动多少个新的实例都可以。
 * @author bwang018 2020/12/18
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}
