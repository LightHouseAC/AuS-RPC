package com.aus.advancedrpc.registry;

import com.aus.advancedrpc.config.RegistryConfig;
import com.aus.advancedrpc.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RegistryTest {

    final Registry registry = new EtcdRegistry();

    @Before
    public void init(){
        RegistryConfig registryConfig = new RegistryConfig();
        registry.init(registryConfig);
    }

    @Test
    public void register() throws Exception{
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("testService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort("18080");
        registry.register(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("testService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort("18081");
        registry.register(serviceMetaInfo);
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("testService");
        serviceMetaInfo.setServiceVersion("1.1");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort("18082");
        registry.register(serviceMetaInfo);
    }

    @Test
    public void serviceDiscovery(){
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("testService");
        serviceMetaInfo.setServiceVersion("1.0");
        String serviceKey = serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceKey);
        Assert.assertNotNull(serviceMetaInfoList);
    }

    @Test
    public void unRegister(){
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("testService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort("18080");
        registry.unregister(serviceMetaInfo);
    }


}
