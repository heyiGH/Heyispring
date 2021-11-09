package com.heyi.service;

import com.spring.*;

@Component("userService")
public class UserServiceImpl implements InitializingBean,UserService,BeanNameAware {

    @Autowired
    private OrderService orderService;


    private String beanName;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        System.out.println("setBeanName");
        this.beanName = beanName;
    }

    @Override
    public void afterProPertiesSet() throws Exception {
        System.out.println("afterProPertiesSet()");
    }

    public void test(){
        System.out.println(orderService);
        System.out.println(beanName);
    }

}
