package com.aus.sample.provider;

import com.aus.advancedrpc.RpcApplication;
import com.aus.advancedrpc.config.RegistryConfig;
import com.aus.advancedrpc.config.RpcConfig;
import com.aus.advancedrpc.model.ServiceMetaInfo;
import com.aus.advancedrpc.registry.LocalRegistry;
import com.aus.advancedrpc.registry.Registry;
import com.aus.advancedrpc.registry.RegistryFactory;
import com.aus.advancedrpc.server.HttpServer;
import com.aus.advancedrpc.server.VertxHttpServer;
import com.aus.sample.common.service.TaskService;
import com.aus.sample.provider.impl.TaskServiceImpl;

public class SampleProvider {
    public static void main(String[] args) {
        RpcApplication.init();
        String serviceName = TaskService.class.getName();
        LocalRegistry.register(serviceName, TaskServiceImpl.class);

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(String.valueOf(rpcConfig.getServerPort()));
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e){
            throw new RuntimeException("注册失败", e);
        }

        HttpServer httpServer = new VertxHttpServer();
        httpServer.startServer(RpcApplication.getRpcConfig().getServerPort());
    }

}