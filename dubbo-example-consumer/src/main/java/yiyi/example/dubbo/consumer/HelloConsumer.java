package yiyi.example.dubbo.consumer;

import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yiyi.example.dubbo.common.HelloService;

/**
 * The consumer.
 * 1-1.retries=3,调用服务异常后，consumer会再重试3次，也就是说会调用服务4此
 *     在负载均衡的情况下，每次retry可能访问不同的实例。
 * 1-2.timeout=1000， consumer调用服务，如果1秒没有反应，则失败。因为配置了retries=3, 所以还会重试3次。
 * 
 * 2-1.dubbo同样的配置即可以在服务定义时@Service去做，也可以在引用服务时@Reference去做，
 *    @Service(version = "${helloService.version}", retries=5,timeout=1000)
 *    @Reference(version = "${helloService.version}",retries=4,timeout=1000)
 *    以哪个为准呢？答案：通过实验得出的结论时，以定义服务时的配置为准。
 * @author bwang018 2020/12/18
 */
@Component
public class HelloConsumer {

    private static final Logger logger = LoggerFactory.getLogger(HelloConsumer.class);

    @Reference(version = "${helloService.version}",retries=4,timeout=1000)
    private HelloService helloService;

//    @Scheduled(fixedDelay = 1000)
    public void run() {
        logger.info("call HelloService: {}", helloService.hello("shuaicj"));
    }
}
