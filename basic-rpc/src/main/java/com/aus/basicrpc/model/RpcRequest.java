package com.aus.basicrpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 5137112355689878831L;

    private String serviceName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] args;

}
