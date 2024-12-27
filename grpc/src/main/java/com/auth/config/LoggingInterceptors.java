package com.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class LoggingInterceptors implements ServerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptors.class);

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        // Log method name and client metadata
        logger.info("Received call to method: {}", call.getMethodDescriptor().getFullMethodName());
        logger.info("Client metadata: {}", headers);

        // Delegate to the next handler
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(next.startCall(call, headers)) {
            @Override
            public void onMessage(ReqT message) {
                // Log the incoming request message
                logger.info("Request message: {}", message);
                super.onMessage(message);
            }

            @Override
            public void onComplete() {
                // Log when the call is completed
                logger.info("Call to method {} completed.", call.getMethodDescriptor().getFullMethodName());
                super.onComplete();
            }
        };
    }

}
