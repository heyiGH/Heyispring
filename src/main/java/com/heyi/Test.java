package com.heyi;

import com.heyi.service.UserService;
import com.spring.MySpringApplicationContext;

public class Test {
    public static void main(String[] args) {
        MySpringApplicationContext mySpringApplicationContext = new MySpringApplicationContext(Config.class);
        UserService userService = (UserService) mySpringApplicationContext.getBean("userService");
        userService.test();//1.代理对象
    }
}
