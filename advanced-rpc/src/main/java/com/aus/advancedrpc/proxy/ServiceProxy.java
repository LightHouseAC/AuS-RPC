package com.aus.advancedrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.aus.advancedrpc.RpcApplication;
import com.aus.advancedrpc.config.RpcConfig;
import com.aus.advancedrpc.constant.RpcConstant;
import com.aus.advancedrpc.model.RpcRequest;
import com.aus.advancedrpc.model.RpcResponse;
import com.aus.advancedrpc.model.ServiceMetaInfo;
import com.aus.advancedrpc.registry.Registry;
import com.aus.advancedrpc.registry.RegistryFactory;
import com.aus.advancedrpc.serializer.Serializer;
import com.aus.advancedrpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try{
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // DONE 调用地址改为从注册中心获取
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(rpcRequest.getServiceName());
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException(String.format("服务 %s 暂无地址", serviceMetaInfo.getServiceName()));
            }
            //  1) 随机获取实现简单负载均衡
            ServiceMetaInfo selectedServiceMetaInfo = RandomUtil.randomEle(serviceMetaInfoList);
            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                    .body(bodyBytes)
                    .execute()){
                byte[] result = httpResponse.bodyBytes();
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
