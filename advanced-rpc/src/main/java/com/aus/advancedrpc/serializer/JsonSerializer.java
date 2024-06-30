package com.aus.advancedrpc.serializer;

import com.aus.advancedrpc.model.RpcRequest;
import com.aus.advancedrpc.model.RpcResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonSerializer implements Serializer{

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    /*
    Deserialize的时候Object的原始对象会被擦除，
    以前用MQ用MessageConverter用Jackson转json格式传的时候传的Long对象就被反序列化成了Integer,
    所以用handle方法特殊处理一下req和res
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes, type);
        if (obj instanceof RpcRequest) return handleRequest((RpcRequest) obj, type);
        if (obj instanceof RpcResponse) return handleResponse((RpcResponse) obj, type);
        return obj;
    }

    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getArgs();
        for(int i=0;i< parameterTypes.length;i++){
            Class<?> clazz = parameterTypes[i];
            //  如果类型不同，就重新处理一下，保证最后返回的结果类型一致
            if (!clazz.isAssignableFrom(args[i].getClass())){
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argBytes, clazz);
            }
        }
        return type.cast(rpcRequest);
    }

    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type) throws IOException {
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(dataBytes, rpcResponse.getDataType()));
        return type.cast(rpcResponse);
    }
}
