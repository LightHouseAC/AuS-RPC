package com.aus.sample.consumer;

import com.aus.advancedrpc.config.RpcConfig;
import com.aus.advancedrpc.constant.RpcConstant;
import com.aus.advancedrpc.utils.ConfigUtils;

public class SampleConsumerConfig {

    public static void main(String[] args) {
        RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        System.out.println(rpcConfig);
    }

}
