package com.cloudbeds.userservice.testutils;

import com.cloudbeds.userservice.grpc.CreateAddressRequest;
import com.cloudbeds.userservice.grpc.CreateUserRequest;

import java.util.UUID;
import java.util.function.Consumer;

public class CreateUserRequestFixtures {

    public static CreateUserRequest createCreateUserRequest() {
        return createCreateUserRequestBuilder()
                .addAddresses(createAddressBuilder().build())
                .build();
    }

    public static CreateUserRequest createUserRequestWithAddress(Consumer<CreateAddressRequest.Builder> enricher) {
        CreateUserRequest.Builder createUserRequestBuilder = createCreateUserRequestBuilder();
        CreateAddressRequest.Builder addressBuilder = createAddressBuilder();
        enricher.accept(addressBuilder);
        createUserRequestBuilder.addAddresses(addressBuilder);
        return createUserRequestBuilder.build();
    }

    private static CreateUserRequest.Builder createCreateUserRequestBuilder() {
        return CreateUserRequest.newBuilder()
                .setFirstName("Wanda")
                .setLastName("Banda")
                .setEmail(UUID.randomUUID() + "@email.com")
                .setPassword("password");
    }

    private static CreateAddressRequest.Builder createAddressBuilder() {
        return CreateAddressRequest.newBuilder()
                .setAddressLine1("line 1")
                .setAddressLine2("line 2")
                .setCity("city")
                .setCountry("country")
                .setState("state")
                .setZip("zip");
    }
}
