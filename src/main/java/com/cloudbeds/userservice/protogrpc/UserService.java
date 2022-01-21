package com.cloudbeds.userservice.protogrpc;

import com.cloudbeds.userservice.domain.User;
import com.cloudbeds.userservice.domain.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
class UserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;
    private final GrpcDomainTransformer transformer;

    @Override
    @Transactional
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> responseObserver) {
        log.info("Creating user: {}", request);

        handleException(() -> {

            UUID id = saveUser(request);
            responseObserver.onNext(CreateUserResponse.newBuilder()
                    .setId(id.toString())
                    .build());
            responseObserver.onCompleted();

            log.info("Created user with id: {}", id);

        }, responseObserver);
    }

    private void handleException(Runnable runnable, StreamObserver responseObserver) {
        try {
            runnable.run();
        } catch (Exception e) {
            responseObserver.onError(e);
            log.error("Exception occurred", e);
        }
    }

    private UUID saveUser(CreateUserRequest request) {
        User user = transformer.mapCreateUserRequestToDomainUser(request);
        User saved = userRepository.save(user);
        return saved.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public void findUsersFromCountry(FindUsersFromCountryRequest request, StreamObserver<UserListResponse> responseObserver) {
        log.info("Finding users from country: {}", request.getCountry());

        handleException(() -> {
            Iterable<User> users = userRepository.findByAddresses_Country(request.getCountry());

            UserListResponse.Builder builder = UserListResponse.newBuilder();
            users.forEach(user -> builder.addUsers(transformer.mapUser(user)));
            UserListResponse userListResponse = builder.build();
            responseObserver.onNext(userListResponse);
            responseObserver.onCompleted();

            log.info("Found {} users from country: {}", userListResponse.getUsersCount(), request.getCountry());
        }, responseObserver);
    }
}
