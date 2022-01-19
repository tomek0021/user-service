package com.cloudbeds.userservice.rest;

import com.cloudbeds.userservice.domain.User;
import com.cloudbeds.userservice.domain.UserRepository;
import com.cloudbeds.userservice.rest.CreateUserRequest;
import com.cloudbeds.userservice.rest.CreateUserResponse;
import com.cloudbeds.userservice.rest.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        log.info("Creating user: {}", request);

        UUID id = saveUser(request);

        responseObserver.onNext(CreateUserResponse.newBuilder()
                .setId(id.toString())
                .build());
        responseObserver.onCompleted();

        log.info("Created user with id: {}", id);
    }

    private UUID saveUser(CreateUserRequest request) {
        User user = mapCreateUserRequestToDomainUser(request);
        User saved = userRepository.save(user);
        return saved.getId();
    }

    private User mapCreateUserRequestToDomainUser(CreateUserRequest request) {
        User user = new User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword());
        user.setAddresses(request.getAddressesList().stream()
                .map(this::mapAddress)
                .collect(Collectors.toList()));
        return user;
    }

    private com.cloudbeds.userservice.domain.Address mapAddress(CreateAddressRequest addressRequest) {
        return com.cloudbeds.userservice.domain.Address.builder()
                .addressLine1(addressRequest.getAddressLine1())
                .addressLine2(addressRequest.getAddressLine2())
                .country(addressRequest.getCountry())
                .state(addressRequest.getState())
                .zip(addressRequest.getZip())
                .city(addressRequest.getCity())
                .build();
    }
}
