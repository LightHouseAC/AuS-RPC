package com.aus.advancedrpc.registry;

import com.aus.advancedrpc.config.RegistryConfig;
import com.aus.advancedrpc.model.ServiceMetaInfo;

import java.util.List;

public interface Registry {

    void init(RegistryConfig registryConfig);

    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    void unregister(ServiceMetaInfo serviceMetaInfo);

    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    void destroy();

    void heartBeat();

}
