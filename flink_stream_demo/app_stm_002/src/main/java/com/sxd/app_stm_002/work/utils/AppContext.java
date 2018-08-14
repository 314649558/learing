package com.sxd.app_stm_002.work.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public final class AppContext {

    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return AppContext.applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContext.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }
}
