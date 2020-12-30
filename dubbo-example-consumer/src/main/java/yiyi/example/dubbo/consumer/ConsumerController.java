package yiyi.example.dubbo.consumer;

import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import yiyi.example.dubbo.common.Hello;
import yiyi.example.dubbo.common.HelloService;

/**
 * @author bwang018 2020/12/18
 */
@RestController
@RequestMapping("/dubbotest")
public class ConsumerController {
	private static final Logger logger = LoggerFactory.getLogger(ConsumerController.class);
	
    @Reference(version = "${helloService.version}")
    private HelloService helloService;

    @GetMapping("/helloinfo")
    public String sayHello() {
    	logger.info("call HelloService: {}", helloService.hello("shuaicj"));
        Hello hello = helloService.hello("shuaicj");
        return hello.getMessage();
    }
}
