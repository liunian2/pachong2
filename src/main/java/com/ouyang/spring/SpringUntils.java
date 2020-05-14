package com.ouyang.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUntils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    /**
     * Desc :  获取applicationContext
     */

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Desc :  通过beanName获取bean
     */
    public static Object getBean(String beanName) {
        return getApplicationContext().getBean(beanName);
    }

    /**
     * Desc :  通过class获取bean
     */
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }


    /**
     * Desc : 通过name、class返回指定的bean
     */
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name,clazz);
    }

}
