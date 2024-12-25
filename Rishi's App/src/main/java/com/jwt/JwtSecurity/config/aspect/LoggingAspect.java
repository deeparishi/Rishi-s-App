package com.jwt.JwtSecurity.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.jwt.JwtSecurity.service.impl.FriendServiceImpl.*(..))")
    public void pointCutForFriendServiceLogger() {}

    @Around("pointCutForFriendServiceLogger()")
    public void log() {
        log.info("Friend Service Connected ");
    }

    @After("execution(* com.jwt.JwtSecurity.service.impl.AuthenticationService.*(..))")
    public void afterLog(JoinPoint joinPoint) {

        /*
            Returns the name of the method being intercepted.
            e.g. login -> method name
         */
        log.info("Auth Service info {} ", joinPoint.getSignature().getName());

        /*
            Returns the fully qualified name of the class that declares the intercepted method
             e.g. com.jwt.JwtSecurity.service.impl.AuthenticationService
         */
        log.info("Auth Declaring Type {} ", joinPoint.getSignature().getDeclaringTypeName());

        /*
            Returns a string representation of the method signature, including its return type and parameters.

               return type                                                                    parameter
             e.g. UserTokenResponse com.jwt.JwtSecurity.service.impl.AuthenticationService.login(UserSignInRequest)

         */
        log.info("Auth Signature info {} ", joinPoint.getSignature().toString());

        /*
            Provides the source location of the intercepted method.
            This might include the class or method name but is often not very detailed in
            Spring AOP because it uses proxies.
             eg org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint$SourceLocationImpl@3efd231c
         */
        log.info("Auth Source Location info {} ", joinPoint.getSourceLocation().toString());

        /*
            Fetches the nesting host of the intercepted method’s class.
            Nesting hosts are relevant in the context of inner/nested classes

            eg class com.jwt.JwtSecurity.service.impl.AuthenticationService
         */
        log.info("Auth Nest Host info {} ", joinPoint.getSourceLocation().getWithinType().getNestHost());

        /*
            Returns the toString() representation of the actual target object (the proxied bean) being intercepted.
            eg com.jwt.JwtSecurity.service.impl.AuthenticationService@74f62c68
         */
        log.info("Auth Target {} ", joinPoint.getTarget().toString());

        /*
            Represents a static description of the join point.
            This usually includes information like the method name, class name, and parameter types
             eg execution(UserTokenResponse com.jwt.JwtSecurity.service.impl.AuthenticationService.login(UserSignInRequest))
         */
        log.info("Auth static part {} ", joinPoint.getStaticPart().toString());

        /*
            Returns the kind of join point.
            For Spring AOP, this will typically return "method-execution" as it mostly intercepts method calls
            eg method-execution
         */
        log.info("Auth kind {} ", joinPoint.getKind());

        /*
            Returns the toString() representation of the proxy object (not the actual target bean but the Spring proxy handling it).
            eg com.jwt.JwtSecurity.service.impl.AuthenticationService@74f62c68
         */
        log.info("Auth This {} ", joinPoint.getThis().toString());

        /*
            Returns an array of arguments passed to the intercepted method
            eg ["user", "password"]
         */
        log.info("Auth args {} ", Arrays.deepToString(joinPoint.getArgs()));
    }

    @Around("execution (* com.jwt.JwtSecurity.service.impl.MailService.*(..))")
    public void logForMail(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Mail triggered {}", Arrays.deepToString(joinPoint.getArgs()));
        long before = System.currentTimeMillis();
        joinPoint.proceed();
        long after = System.currentTimeMillis();
        log.info("Time taken to send this mail {}", after - before);
    }

    // Will work if method doesn't throw any error
    @AfterReturning(pointcut = "execution (* com.jwt.JwtSecurity.service.impl.MailService.*(..))")
    public void afterSuccessfulMail() {
        log.info("Mail Sent Successfully...");
    }

    // Will work if method throws any error
    @AfterThrowing("execution (* com.jwt.JwtSecurity.service.impl.MailService.*(..))")
    public void ifMailFailed() {
        log.info("Issue with sending mail");
    }

    // Will work if error occurred or not like finally
    @After("execution (* com.jwt.JwtSecurity.service.impl.MailService.*(..))")
    public void whateverMailSentOrNot() {
        log.info("Not sure Mail Sent Successfully or not...");
    }

    // Below methods will execute other services except mail service
    @Pointcut("execution(* com.jwt.JwtSecurity.service.impl.*.*(..)) && !execution(* com.jwt.JwtSecurity.service.impl.MailService.*(..))")
    public void excludeMailService() {}

    @Before("excludeMailService()")
    public void logForOtherServices() {
        log.info("This advice is not applied to MailService");
    }


    // This will work for what are the classes annotated with @Service annotation
    @Before("@within(org.springframework.stereotype.Service)")
    public void logForServiceLayer() {
        log.info("Service annotation classes invoked");
    }


    @Before("within(com.jwt.JwtSecurity.service.impl.*)")
    public void logWithin() {
        log.info("Method within a class in the service.impl package was called");
    }

    // Matches join points where the proxy object is an instance of the specified type
    @Before("this(com.jwt.JwtSecurity.service.MailService)")
    public void logThisProxy() {
        log.info("Proxy is an instance of MailService");
    }

    // Matches join points where the method arguments are instances of the given type
    @Before("args(java.lang.String, int)")
    public void logMethodArgs() {
        log.info("Method with arguments String and int was called");
    }

    @Pointcut("")
    public void withinServicePackage() {}

    // It'll apply the advice to all methods in the services defined in the com.xyz.service package but exclude those in its sub-packages.
    @Before("within(com.jwt.JwtSecurity.service.*)")
    public void logWithinServicePackage() {
        log.info("Method within com.xyz.service package was called.");
    }


    // It'll  apply advice to methods in all classes in the com.xyz.service package, including those in sub-packages.
    @Before("com.jwt.JwtSecurity.service..")
    public void logWithinServiceAndSubPackages() {
        log.info("Method within com.xyz.service or its sub-packages was called.");
    }

    @After("execution(public * *(..))")
    public void executesOnAnyPublicMethodWithinProject() {}

    @After("execution(* set*(..))")
    public void executesIfAnyMethodNameStartsWithSet() {}

}


/* NOTES
        Use @Around sparingly as it has more performance overhead compared to other advice types. Prefer @Before, @After, or @AfterReturning for most use cases.
        Combine different expressions using logical operators like &&, ||, and !

        Expression	Key Aspect	                                        Example Match
        within	    Matches methods within a specific type/package.	    Methods in com.jwt.JwtSecurity.service.impl.*
        this	    Matches based on the proxy type.	                Proxy object is MailService.
        target	    Matches based on the target object type.	        Target object is MailService.
        args	    Matches based on method arguments.	                Method with String and int args.
        @target	    Matches based on annotations on the target class.	Target class is annotated with @Service.


        Joint Point
        A join point is a specific point in the execution of your application where some behavior can be "hooked" or "intercepted" by Aspect-Oriented Programming (AOP).
        Think of it as a place in your code where extra logic (like logging, security checks, or performance monitoring) can be added without modifying the original code.
 */