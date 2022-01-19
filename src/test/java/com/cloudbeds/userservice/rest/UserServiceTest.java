package com.cloudbeds.userservice.rest;

import com.cloudbeds.userservice.domain.Address;
import com.cloudbeds.userservice.domain.User;
import com.cloudbeds.userservice.domain.UserRepository;
import com.cloudbeds.userservice.rest.CreateUserRequest;
import com.cloudbeds.userservice.rest.CreateUserResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private UserService userService = new UserService(userRepository);

    private StreamObserver<CreateUserResponse> responseObserver = mock(StreamObserver.class);

    @BeforeEach
    public void prepare() {
        when(userRepository.save(any())).thenAnswer(i -> {
            User user = i.getArgument(0, User.class);
            user.setId(UUID.randomUUID());
            return user;
        });
    }

    @Test
    void createUserSetUserIdInResponse() {
        // given
        CreateUserRequest request = createCreateUserRequest();

        // when
        userService.createUser(request, responseObserver);

        // then
        ArgumentCaptor<CreateUserResponse> responseCaptor = ArgumentCaptor.forClass(CreateUserResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        CreateUserResponse response = responseCaptor.getValue();
        assertThat(response.getId()).isNotEmpty();
        assertThat(UUID.fromString(response.getId())).isNotNull();

        verify(responseObserver).onCompleted();
    }

    @Test
    void createUserMapsCreateUserRequestIntoDomainUser() {
        // given
        CreateUserRequest request = createCreateUserRequest();

        // when
        userService.createUser(request, responseObserver);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User user = userCaptor.getValue();
        assertThat(user.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(user.getLastName()).isEqualTo(request.getLastName());
        assertThat(user.getEmail()).isEqualTo(request.getEmail());
        assertThat(user.getPassword()).isEqualTo(request.getPassword());

        assertThat(user.getAddresses()).hasSize(1);
        Address address = user.getAddresses().get(0);
        assertThat(address.getAddressLine1()).isEqualTo("line 1");
        assertThat(address.getAddressLine2()).isEqualTo("line 2");
        assertThat(address.getCity()).isEqualTo("city");
        assertThat(address.getCountry()).isEqualTo("country");
        assertThat(address.getState()).isEqualTo("state");
        assertThat(address.getZip()).isEqualTo("zip");
    }

    private CreateUserRequest createCreateUserRequest() {
        return CreateUserRequest.newBuilder()
                .setFirstName("Wanda")
                .setLastName("Banda")
                .setEmail("email")
                .setPassword("password")
                .addAddresses(CreateAddressRequest.newBuilder()
                        .setAddressLine1("line 1")
                        .setAddressLine2("line 2")
                        .setCity("city")
                        .setCountry("country")
                        .setState("state")
                        .setZip("zip")
                        .build())
                .build();
    }
}
