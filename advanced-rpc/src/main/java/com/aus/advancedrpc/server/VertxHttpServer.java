package com.aus.advancedrpc.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer{

    @Override
    public void startServer(int port) {
        Vertx vertx = Vertx.vertx();
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        server.requestHandler(new HttpServerHandler());

        server.listen(port, result -> {
            if (result.succeeded()){
                System.out.println("Server is now listening on port: " + port);
            }else{
                System.out.println("Failed to start server" + result.cause());
            }
        });
    }
}
