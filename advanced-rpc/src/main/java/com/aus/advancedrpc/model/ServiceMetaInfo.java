package com.aus.advancedrpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class ServiceMetaInfo {

    private String serviceName;

    private String serviceVersion = "1.0";

    private String serviceHost;

    private String servicePort;

    /**
     * 获取服务Key：设计：业务名：业务版本
     * @return 服务key
     */
    public String getServiceKey(){
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取服务节点的Key：设计：业务Key/地址
     * @return 节点key
     */
    public String getServiceNodeKey(){
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    public String getServiceAddress(){
        if (!StrUtil.contains(serviceHost, "http")){
            return String.format("http://%s:%s",serviceHost, servicePort);
        }
        return String.format("%s:%s",serviceHost,servicePort);
    }

}
