package com.aus.advancedrpc.server;

import com.aus.advancedrpc.RpcApplication;
import com.aus.advancedrpc.model.RpcRequest;
import com.aus.advancedrpc.model.RpcResponse;
import com.aus.advancedrpc.registry.LocalRegistry;
import com.aus.advancedrpc.serializer.JdkSerializer;
import com.aus.advancedrpc.serializer.Serializer;

import com.aus.advancedrpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        System.out.println("Request received: " + request.method() + " " + request.uri());

        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try{
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e){
                e.printStackTrace();
            }

            RpcResponse rpcResponse = new RpcResponse();
            if (rpcRequest == null){
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try{
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());

                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e){
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(request, rpcResponse, serializer);
        });
    }

    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer){
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try{
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e){
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }

}
