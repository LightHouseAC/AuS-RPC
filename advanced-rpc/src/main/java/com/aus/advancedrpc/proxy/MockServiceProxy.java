package com.aus.advancedrpc.proxy;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class MockServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    private Object getDefaultObject(Class<?> type){
        if (type.isPrimitive()){
            if (type == boolean.class){
                return RandomUtil.randomBoolean();
            } else if (type == short.class) {
                return RandomUtil.randomBigDecimal().shortValue();
            } else if (type == int.class) {
                return RandomUtil.randomInt();
            } else if(type == long.class){
                return RandomUtil.randomLong();
            } else if (type == double.class) {
                return RandomUtil.randomDouble();
            }
        }
        return null;
    }

}
