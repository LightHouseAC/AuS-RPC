package com.aus.advancedrpc;

import com.aus.advancedrpc.config.RegistryConfig;
import com.aus.advancedrpc.config.RpcConfig;
import com.aus.advancedrpc.constant.RpcConstant;
import com.aus.advancedrpc.registry.EtcdRegistry;
import com.aus.advancedrpc.registry.Registry;
import com.aus.advancedrpc.registry.RegistryFactory;
import com.aus.advancedrpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Double-Checked Lock
 * 双检锁实现单例模式创建Config
 * 为什么这个Config使用单例模式？避免重复地创建Config大量消耗内存
 */
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());

        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        //  通过传入需要构建的注册中心名字创建注册中心
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);
    }

    public static void init(){
        RpcConfig newRpcConfig;
        try{
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e){
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    public static RpcConfig getRpcConfig(){
        if(rpcConfig==null){
            synchronized (RpcApplication.class){
                if(rpcConfig == null)init();
            }
        }
        return rpcConfig;
    }

}
