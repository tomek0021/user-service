package com.cloudbeds.userservice.avrogrpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.grpc.AvroGrpcServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
class AvroGrpcServerRunner implements SmartLifecycle {

    private final AvroUserService userService;
    private final int port;

    private Server server;

    AvroGrpcServerRunner(AvroUserService userService, @Value("${user-service.avro-grpc.port}") int port) {
        this.userService = userService;
        this.port = port;
    }

    @Override
    public void start() {
        ServerServiceDefinition serviceDefinition = AvroGrpcServer.createServiceDefinition(UserService.class, userService);
        server = ServerBuilder
                .forPort(port)
                .addService(serviceDefinition)
                .build();
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException("Can't start avro grpc user service.", e);
        }

        log.info("Started avro grpc user service successfully on port{}.", port);
    }

    @Override
    public void stop() {
        server.shutdown();
        log.info("Stopped avro grpc user service successfully.");
    }

    @Override
    public boolean isRunning() {
        return server != null && !(server.isShutdown() || server.isTerminated());
    }
}
