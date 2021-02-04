package com.shreyansh.crm.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.logging.Logger;

@Component
@Aspect
public class CRMLoggingAspect {

    Logger logger = Logger.getLogger(CRMLoggingAspect.class.getName());

    @Pointcut("execution(* com.shreyansh.crm.controller.*.*(..))")
    private void forControllerPackage() {
    }

    @Pointcut("execution(* com.shreyansh.crm.service.*.*(..))")
    private void forServicePackage() {
    }

    @Pointcut("execution(* com.shreyansh.crm.dao.*.*(..))")
    private void forDAOPackage() {
    }

    @Pointcut("forControllerPackage() || forServicePackage() || forDAOPackage()")
    private void forAppFlow() {
    }

    @Before("forAppFlow()")
    public void before(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toString();

        logger.info("=====>> In @Before calling method " + method);

        Object[] args = joinPoint.getArgs();

        logger.info("=====>> Arguments: " + args.length);

        Arrays.asList(args).forEach(e -> logger.info(e.toString() + "\n"));
    }

    @AfterReturning(pointcut = "forAppFlow()",
            returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        String method = joinPoint.getSignature().toString();

        logger.info("=====>> In @AfterReturning calling method " + method);


        logger.info("=====>> Data Returned: " + result);

    }


}
