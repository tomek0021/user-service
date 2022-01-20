package com.cloudbeds.userservice.grpc;

import com.cloudbeds.userservice.domain.User;
import com.cloudbeds.userservice.domain.UserRepository;
import com.cloudbeds.userservice.testutils.UserFixtures;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static com.cloudbeds.userservice.testutils.CreateUserRequestFixtures.createCreateUserRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private GrpcDomainTransformer transformer = new GrpcDomainTransformer();
    private UserService userService = new UserService(userRepository, transformer);

    @BeforeEach
    public void prepare() {
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User user = i.getArgument(0, User.class);
            user.setId(UUID.randomUUID());
            return user;
        });
    }

    @Test
    void createUserSetUserIdInResponse() {
        // given
        CreateUserRequest request = createCreateUserRequest();
        StreamObserver<CreateUserResponse> responseObserver = mock(StreamObserver.class);

        // when
        userService.createUser(request, responseObserver);

        // then
        CreateUserResponse response = captureResponse(responseObserver, CreateUserResponse.class);
        assertThat(response.getId()).isNotEmpty();
        assertThat(UUID.fromString(response.getId())).isNotNull();

        verify(responseObserver).onCompleted();
    }

    @Test
    void onExceptionCallStreamObserver() {
        // given
        CreateUserRequest request = createCreateUserRequest();
        StreamObserver<CreateUserResponse> responseObserver = mock(StreamObserver.class);
        RuntimeException exception = new RuntimeException("kaboom!");
        when(userRepository.save(any(User.class))).thenThrow(exception);

        // when
        userService.createUser(request, responseObserver);

        // then
        verify(responseObserver).onError(exception);
    }

    @Test
    void findUsersFromCountryMapProperValues() {
        // given
        FindUsersFromCountryRequest request = FindUsersFromCountryRequest.newBuilder().setCountry("Whatever").build();
        StreamObserver<UserListResponse> responseObserver = mock(StreamObserver.class);
        List<User> users = UserFixtures.createUsersWithAddresses(10, 2);
        when(userRepository.findByAddresses_Country(anyString())).thenReturn(users);

        // when
        userService.findUsersFromCountry(request, responseObserver);

        // then
        UserListResponse response = captureResponse(responseObserver, UserListResponse.class);
        assertThat(response.getUsersList()).hasSize(10);

        verify(responseObserver).onCompleted();
    }

    private <T> T captureResponse(StreamObserver<T> responseObserver, Class<T> clazz) {
        ArgumentCaptor<T> responseCaptor = ArgumentCaptor.forClass(clazz);
        verify(responseObserver).onNext(responseCaptor.capture());
        return responseCaptor.getValue();
    }


}
