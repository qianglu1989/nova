package com.redefine.nove.abtest;

import com.redefine.nove.NoveTestContext;
import com.redefine.nove.NoveTestContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

/**
 * @author QIANGLU
 */
@Aspect
public class NoveTestInterceptor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private NoveTestContext noveTestContext;

    public NoveTestInterceptor(NoveTestContext noveTestContext) {
        this.noveTestContext = noveTestContext;
    }


    @Pointcut("@annotation(NoveTest)")
    public void anyMethod() {

    }


    @Before(value = "anyMethod()")
    public void doBefore(JoinPoint jp) {

        long start = System.currentTimeMillis();
        try {
            MethodSignature methodSig = (MethodSignature) jp.getSignature();
            Annotation[] annotations = methodSig.getMethod().getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                String name = annotation.annotationType().getName();
                String novetest = NoveTest.class.getName();
                if (novetest.equals(name)) {
                    NoveTest test = (NoveTest) annotation;
                    NoveTestContextHolder.initBucketData(test.name(), noveTestContext);
                }
            }
        } catch (Exception e) { //防御性容错

        }
        logger.debug("NoveTestInterceptor duration:{}", System.currentTimeMillis() - start);
    }


    @After(value = "anyMethod()")
    public void doAfter(JoinPoint jp) {

    }


    @AfterThrowing(value = "anyMethod()")
    public void doThrow(JoinPoint jp) {
        //TODO send kafka msg
    }

}
