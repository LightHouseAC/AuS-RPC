package com.aus.advancedrpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = 3051168901522233797L;

    private Object Data;

    private Class<?> dataType;

    private String message;

    private Exception exception;

}
