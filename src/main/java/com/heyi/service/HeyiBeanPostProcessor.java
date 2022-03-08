package com.heyi.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class HeyiBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前");
        if (beanName.equals("userService")){
            ((UserServiceImpl)bean).setBeanName("heyihaha");
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        if (beanName.equals("userService")){
            Object proxyInstance = Proxy.newProxyInstance(HeyiBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(),(Object proxy, Method method, Object[] args)-> {
                    System.out.println("代理逻辑");

                    return method.invoke(bean,args);
            });
            return proxyInstance;
        }
        return bean;
    }
}
