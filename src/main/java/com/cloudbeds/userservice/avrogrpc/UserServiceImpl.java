package com.cloudbeds.userservice.avrogrpc;

import com.cloudbeds.userservice.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User getUser(String id) {
        log.info("Getting user: {}", id);

        return mapUser(userRepository.findById(UUID.fromString(id)).orElse(null));
    }

    User mapUser(com.cloudbeds.userservice.domain.User user) {
        User.Builder builder = User.newBuilder()
                .setId(user.getId().toString())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword());
        builder.setAddresses(
                user.getAddresses().stream()
                        .map(address -> mapAddress(address))
                        .collect(Collectors.toList())
        );
        return builder.build();
    }

    private Address mapAddress(com.cloudbeds.userservice.domain.Address address) {
        return Address.newBuilder()
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
