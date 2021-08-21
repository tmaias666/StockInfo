package com.ty.aspect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Order(1)
@Component
public class PrivilegeAspect{

    private static final Logger logger = LoggerFactory.getLogger(PrivilegeAspect.class);

    @Pointcut("execution(* com.ty.controller.StockInfoController..*(..)) ")
    public void pointcut(){
    }

    @Around("pointcut()")
    public Object parseAccessToken(ProceedingJoinPoint pjp) throws Throwable{
        //Object[] signatureArgs = pjp.getArgs();
        //String sessionId = (String) signatureArgs[0];
        try{
            HttpServletRequest request = ( (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes() ).getRequest();
            verifyPrivilegeViaAccount(request);
            return pjp.proceed();
        }catch(Exception e){
            logger.error("verify authentication error: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized accessToken!");
        }
    }

    private void verifyPrivilegeViaAccount(HttpServletRequest request) throws Exception{
        String accessToken = request.getHeader("accessToken");
        if (accessToken == null || "".equals(accessToken)){
            throw new Exception("unauthorized accessToken!");
        }else{
            HttpSession session = request.getSession();
            session.setAttribute("accessToken", accessToken);
        }
    }
}