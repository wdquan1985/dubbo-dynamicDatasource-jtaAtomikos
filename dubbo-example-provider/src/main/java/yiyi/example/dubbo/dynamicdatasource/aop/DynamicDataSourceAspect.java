package yiyi.example.dubbo.dynamicdatasource.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import yiyi.example.dubbo.dynamicdatasource.annotation.DS;
import yiyi.example.dubbo.dynamicdatasource.constants.DataSourceConstants;
import yiyi.example.dubbo.dynamicdatasource.context.DynamicDataSourceContextHolder;

import java.util.Objects;

/**
 * TODO
 * 
 * @author bwang018 2020/12/19
 **/
@Aspect
@Component
@Order(1)
public class DynamicDataSourceAspect {

//    @Around("execution(* yiyi.example.dubbo.dynamicdatasource.mapper.ProductMapper.*(..))")
//    public Object aroundProduct(ProceedingJoinPoint joinPoint) throws Throwable {
////        String dsKey = getDSAnnotation(joinPoint).value();
////        System.out.println("Debug: the datasource key: " + dsKey);
//        DynamicDataSourceContextHolder.setContextKey(DataSourceConstants.DS_KEY_MASTER);
//        try{
//        	//织入，把需要执行的被代理对象的方法像针线一样，织入到需要执行的额外代码中间
//        	//这里其实就是执行了被代理对象的方法。
//        	/*
//        	 * ******
//        	 * 本例中，是把被代理对象的方法，织入到设置数据源的代码当中，先设置好了数据源，然后再执行数据库的增删改查。
//        	 * 在执行增删改查的过程中，已经在ThreadLocal中为该线程设置了数据源的名字(String),然后DataSource路由根据这个名字，找到对应的数据源。
//        	 */
//            return joinPoint.proceed();
//        }finally {
//            DynamicDataSourceContextHolder.removeContextKey();
//        }
//    }
    
//    @Around("execution(* yiyi.example.dubbo.dynamicdatasource.mapper.TestUserMapper.*(..))")
//    public Object aroundUser(ProceedingJoinPoint joinPoint) throws Throwable {
////        String dsKey = getDSAnnotation(joinPoint).value();
////        System.out.println("Debug: the datasource key: " + dsKey);
//        DynamicDataSourceContextHolder.setContextKey(DataSourceConstants.DS_KEY_SLAVE);
//        try{
//        	//织入，把需要执行的被代理对象的方法像针线一样，织入到需要执行的额外代码中间
//        	//这里其实就是执行了被代理对象的方法。
//        	/*
//        	 * ******
//        	 * 本例中，是把被代理对象的方法，织入到设置数据源的代码当中，先设置好了数据源，然后再执行数据库的增删改查。
//        	 * 在执行增删改查的过程中，已经在ThreadLocal中为该线程设置了数据源的名字(String),然后DataSource路由根据这个名字，找到对应的数据源。
//        	 */
//            return joinPoint.proceed();
//        }finally {
//            DynamicDataSourceContextHolder.removeContextKey();
//        }
//    }
	
    @Pointcut("@annotation(yiyi.example.dubbo.dynamicdatasource.annotation.DS)")
    public void dataSourcePointCut(){

    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String dsKey = getDSAnnotation(joinPoint).value();
        DynamicDataSourceContextHolder.setContextKey(dsKey);
        try{
            return joinPoint.proceed();
        }finally {
            DynamicDataSourceContextHolder.removeContextKey();
        }
    }

    /**
     * 根据类或方法获取数据源注解
     * @param joinPoint
     * @return
     */
    private DS getDSAnnotation(ProceedingJoinPoint joinPoint){
        Class<?> targetClass = joinPoint.getTarget().getClass();
        DS dsAnnotation = targetClass.getAnnotation(DS.class);
        // 先判断类的注解，再判断方法注解
        if(Objects.nonNull(dsAnnotation)){
            return dsAnnotation;
        }else{
            MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
            return methodSignature.getMethod().getAnnotation(DS.class);
        }
    }
}
