package com.cloudbeds.userservice.protogrpc;

import com.cloudbeds.userservice.domain.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
class GrpcDomainTransformer {

    com.cloudbeds.userservice.domain.User mapCreateUserRequestToDomainUser(CreateUserRequest request) {
        com.cloudbeds.userservice.domain.User user = new com.cloudbeds.userservice.domain.User(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPassword());
        user.setAddresses(request.getAddressesList().stream()
                .map(a -> mapCreateAddressRequest(a, user))
                .collect(Collectors.toList()));
        return user;
    }

    private com.cloudbeds.userservice.domain.Address mapCreateAddressRequest(CreateAddressRequest addressRequest, User user) {
        return com.cloudbeds.userservice.domain.Address.builder()
                .user(user)
                .addressLine1(addressRequest.getAddressLine1())
                .addressLine2(addressRequest.getAddressLine2())
                .country(addressRequest.getCountry())
                .state(addressRequest.getState())
                .zip(addressRequest.getZip())
                .city(addressRequest.getCity())
                .build();
    }

    com.cloudbeds.userservice.protogrpc.User mapUser(User user) {
        com.cloudbeds.userservice.protogrpc.User.Builder builder = com.cloudbeds.userservice.protogrpc.User.newBuilder()
                .setId(user.getId().toString())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword());
        user.getAddresses().forEach(address -> builder.addAddresses(mapAddress(address)));
        return builder.build();
    }

    private com.cloudbeds.userservice.protogrpc.Address mapAddress(com.cloudbeds.userservice.domain.Address address) {
        return com.cloudbeds.userservice.protogrpc.Address.newBuilder()
                .setId(address.getId().toString())
                .setAddressLine1(address.getAddressLine1())
                .setAddressLine2(address.getAddressLine2())
                .setCountry(address.getCountry())
                .setCity(address.getCity())
                .setState(address.getState())
                .setZip(address.getZip())
                .build();
    }
}
