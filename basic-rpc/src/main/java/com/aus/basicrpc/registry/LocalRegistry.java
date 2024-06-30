package com.aus.basicrpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {

    public static final Map<String, Class<?>> serviceReflect = new ConcurrentHashMap<>();

    public static void register(String serviceName, Class<?> implClass){
        serviceReflect.put(serviceName, implClass);
    }

    public static Class<?> get(String serviceName){
        return serviceReflect.get(serviceName);
    }

    public static void remove(String serviceName){
        serviceReflect.remove(serviceName);
    }

}
