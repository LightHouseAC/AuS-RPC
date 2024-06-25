package com.aus.sample.provider;

import com.aus.basicrpc.registry.LocalRegistry;
import com.aus.basicrpc.server.HttpServer;
import com.aus.basicrpc.server.VertxHttpServer;
import com.aus.sample.common.service.TaskService;
import com.aus.sample.provider.impl.TaskServiceImpl;

public class SampleProvider {

    public static void main(String[] args) {
        LocalRegistry.register(TaskService.class.getName(), TaskServiceImpl.class);

        HttpServer httpServer = new VertxHttpServer();
        httpServer.startServer(8080);
    }

}