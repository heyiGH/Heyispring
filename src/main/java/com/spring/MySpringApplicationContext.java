package com.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MySpringApplicationContext {
    private Class configClass;

    // 单例池用于存放单例对象
    private ConcurrentHashMap<String,Object> singletonObjects = new ConcurrentHashMap<String, Object>();
    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public MySpringApplicationContext(Class configClass){
        this.configClass = configClass;

        // 解析配置类
        // ComponentScan注解---->扫描路径---->扫描----->BeanDefinition----->DeanDefinitionMap
        scan(configClass);

        for (String beanName: beanDefinitionMap.keySet()){
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

            if(beanDefinition.getScope().equals("singleton")){
                Object bean = createBean(beanName,beanDefinition);// 单例bean
                singletonObjects.put(beanName,bean);
            }

        }

    }

    public Object createBean(String beanName,BeanDefinition beanDefinition){

        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            // 依赖注入
            for (Field declaredField: clazz.getDeclaredFields()){
                if (declaredField.isAnnotationPresent(Autowired.class)){

                    Object bean = getBean(declaredField.getName());
                    declaredField.setAccessible(true);
                    declaredField.set(instance,bean);

                }
            }
            // Aware 回调
            if (instance instanceof BeanNameAware){
                ((BeanNameAware)instance).setBeanName(beanName);
            }


            // BeanPostProcessor
            for (BeanPostProcessor beanPostProcessor:beanPostProcessorList){
                instance = beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
            }

            // 初始化
            if (instance instanceof InitializingBean){
                try {
                    ((InitializingBean)instance).afterProPertiesSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // BeanPostProcessor
            for (BeanPostProcessor beanPostProcessor:beanPostProcessorList){
                instance = beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            }


            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void scan(Class configClass){

        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();
        path = path.replace(".","/");

        // 扫描
        // Bootstrap---> jre/lib
        // Ext --------> jre/ext/lib
        // App --------> classpath ----->
        ClassLoader classLoader = MySpringApplicationContext.class.getClassLoader();//app
        URL resource = classLoader.getResource(path);
        File file = new File(resource.getFile());
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f:files){
                String fileName = f.getAbsolutePath();
                if(fileName.endsWith(".class")) {
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("\\", ".");

                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (clazz.isAnnotationPresent(Component.class)){

                            if(BeanPostProcessor.class.isAssignableFrom(clazz)){
                                BeanPostProcessor instance = (BeanPostProcessor)clazz.getDeclaredConstructor().newInstance();
                                beanPostProcessorList.add(instance);
                            }


                            // 表示当前这个类是一个bean配置类
                            // 解析类，判断当前bean是单例还是prototype的bean ->  BeanDefinition
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName =  componentAnnotation.value();

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)){
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            }else{
                                beanDefinition.setScope("singleton");
                            }
                            beanDefinitionMap.put(beanName,beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Object getBean(String beanName){
        if (beanDefinitionMap.containsKey(beanName)){
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")){
                Object o = singletonObjects.get(beanName);
                return o;
            }else{
                // 创建一个bean
                return createBean(beanName,beanDefinition);
            }

        }else {
            // 不存在该bean
            throw new NullPointerException();
        }
    }
}
