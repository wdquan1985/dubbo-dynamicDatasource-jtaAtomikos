package yiyi.example.dubbo.dynamicdatasource.service;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yiyi.example.dubbo.common.Hello;
import yiyi.example.dubbo.common.HelloService;

/**
 * Implementation of HelloService.
 * 1-1.retries=3,调用服务异常后，consumer会再重试3次，也就是说会调用服务4次
 *     在负载均衡的情况下，每次retry可能访问不同的实例。
 * 1-2.timeout=1000， consumer调用服务，如果1秒没有反应，则失败。因为配置了retries=3, 所以还会重试3次。
 * 
 * 2-1.dubbo同样的配置即可以在服务定义时@Service去做，也可以在引用服务时@Reference去做，
 *    @Service(version = "${helloService.version}", retries=5,timeout=1000)
 *    @Reference(version = "${helloService.version}",retries=4,timeout=1000)
 *    以哪个为准呢？答案：通过实验得出的结论时，以定义服务时的配置为准。
 *    
 * @author bwang018 2020/12/18
 */

@Service(version = "${helloService.version}", retries=5,timeout=1000)
public class HelloServiceImpl implements HelloService {

    private static final AtomicLong counter = new AtomicLong();
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public Hello hello(String name) {
    	logger.info("invoke the dubbo provider 2");
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new Hello(counter.getAndIncrement(), "Hello3 " + name + "!");
    }
}
