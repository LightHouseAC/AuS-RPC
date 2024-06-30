package com.aus.advancedrpc.config;

import com.aus.advancedrpc.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {

    private String name = "aus-rpc";

    private String version = "1.0-snapshot";

    private String serverHost = "localhost";

    private Integer serverPort = 8080;

    private boolean mock = false;

    private String serializer = SerializerKeys.JDK;

    private RegistryConfig registryConfig = new RegistryConfig();

}
