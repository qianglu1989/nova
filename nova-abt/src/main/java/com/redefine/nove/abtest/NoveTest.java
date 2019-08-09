package com.redefine.nove.abtest;

import java.lang.annotation.*;

/**
 * 用于进行A/B TEST 测试的业务逻辑
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface NoveTest {

    /**
     * 用于提供func 名称标识
     * @return
     */
    String[] name() default {};

    /**
     * 用于提供test名称标识，被标识的名称将使用独立方法调用解析
     * @return
     */
    String[] testName() default {};

    /**
     * 是否打开单一类，进行ABT测试
     * @return
     */
    boolean openSingleConfig() default false;
}
