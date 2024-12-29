package com.auth;

import java.io.IOException;

import javax.sql.DataSource;

import com.auth.config.DatabaseConfig;
import com.auth.config.LoggingInterceptors;
import com.auth.repositories.UserRepositoryImpl;
import com.auth.service.UserServiceImpl;
import com.zaxxer.hikari.HikariDataSource;

import io.github.cdimascio.dotenv.Dotenv;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Load environment variables
        Dotenv dotenv = Dotenv.configure().load();

        // Create DataSource using environment variables
        DataSource dataSource = DatabaseConfig.getDataSource();

        // Initialize repository and service
        UserRepositoryImpl userRepository = new UserRepositoryImpl(dataSource);

        // Get the server port from environment variables with a default value
        int port = Integer.parseInt(dotenv.get("GRPC_SERVER_PORT", "50056"));

        // Build and start gRPC server
        Server server = ServerBuilder.forPort(port)
                .addService((BindableService) new UserServiceImpl(userRepository))
                .intercept(new LoggingInterceptors())
                .build();

        // Add shutdown hook to clean up resources (close DataSource)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (dataSource instanceof HikariDataSource) {
                    ((HikariDataSource) dataSource).close(); // Close HikariCP pool
                }
                server.shutdown();
                System.out.println("gRPC server and database pool shutdown.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        server.start();
        System.out.println("Starting server... at port " + port);
        server.awaitTermination();
    }
}
