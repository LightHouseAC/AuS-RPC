package com.aus.advancedrpc.config;

import lombok.Data;

@Data
public class RegistryConfig {

    private String registry = "etcd";

    private String address = "http://localhost:2379";

    private String username;

    private String password;

    private Long timeout = 5000L;

}
