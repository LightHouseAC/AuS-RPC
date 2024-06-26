package com.aus.sample.provider;

import com.aus.advancedrpc.registry.LocalRegistry;
import com.aus.advancedrpc.server.HttpServer;
import com.aus.advancedrpc.server.VertxHttpServer;
import com.aus.sample.common.service.TaskService;
import com.aus.sample.provider.impl.TaskServiceImpl;

public class SampleProvider {

    public static void main(String[] args) {
        LocalRegistry.register(TaskService.class.getName(), TaskServiceImpl.class);

        HttpServer httpServer = new VertxHttpServer();
        httpServer.startServer(8080);
    }

}