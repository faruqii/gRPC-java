package com.auth.service;

import java.util.Set;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import com.auth.dto.DTOCreateUserRequest;
import com.auth.dto.DTOCreateUserResponse;
import com.auth.entities.User;
import com.auth.repositories.UserRepository;
import com.example.user.CreateUserRequest;
import com.example.user.CreateUserResponse;
import com.example.user.UserServiceGrpc;

import io.grpc.stub.StreamObserver;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // DTO
    private DTOCreateUserRequest mapCreateUserRequest(CreateUserRequest req) {
        DTOCreateUserRequest createUserRequest = new DTOCreateUserRequest();
        createUserRequest.setName(req.getName());
        createUserRequest.setEmail(req.getEmail());
        createUserRequest.setPassword(req.getPassword());
        return createUserRequest;
    }

    private DTOCreateUserResponse mapCreateUserResponse(User user) {
        DTOCreateUserResponse createUserResponse = new DTOCreateUserResponse();
        createUserResponse.setId(user.getId());
        createUserResponse.setName(user.getName());
        createUserResponse.setEmail(user.getEmail());
        return createUserResponse;
    }

    // Validate request
    private void validateRequest(DTOCreateUserRequest req) {
        ValidatorFactory factory = Validation.byDefaultProvider()
        .configure()
        .messageInterpolator(new ParameterMessageInterpolator())
        .buildValidatorFactory();
        
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<DTOCreateUserRequest>> violations = validator.validate(req);

        if (!violations.isEmpty()) {
            StringBuilder errors = new StringBuilder();
            for (ConstraintViolation<DTOCreateUserRequest> violation : violations) {
                errors.append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException("User validation failed: \n" + errors.toString());
        }
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        try {
            // Request
            DTOCreateUserRequest createUserRequest = mapCreateUserRequest(request);
            validateRequest(createUserRequest);

            // Check if user already exists
            User existingUser = userRepository.fincByEmail(createUserRequest.getEmail());
            if (existingUser != null) {
                throw new IllegalArgumentException("User already exists!");
            }

            // Save user to db
            User user = createUserRequest.toUser();
            userRepository.createUser(user);

            // Response
            DTOCreateUserResponse createUserResponse = mapCreateUserResponse(user);
            CreateUserResponse grpcResponse = CreateUserResponse.newBuilder()
                    .setMessage("User created successfully!")
                    .setUserResponse(com.example.user.UserResponse.newBuilder()
                            .setId(createUserResponse.getId().toString())
                            .setName(createUserResponse.getName())
                            .setEmail(createUserResponse.getEmail())
                            .build())
                    .build();

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

}
