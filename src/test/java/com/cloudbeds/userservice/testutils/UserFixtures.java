package com.cloudbeds.userservice.testutils;

import com.cloudbeds.userservice.domain.Address;
import com.cloudbeds.userservice.domain.User;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.cloudbeds.userservice.testutils.AddressFixtures.NO_OP;

public class UserFixtures {

    public static User createUser() {
        String email = UUID.randomUUID() + "@skynet.com";
        User user = new User("John", "Connor", email, "password");
        user.setId(UUID.randomUUID());
        return user;
    }

    public static User createUserWithAddresses(int addressCount, BiConsumer<Integer, Address.AddressBuilder> enricher) {
        User user = createUser();
        user.setAddresses(AddressFixtures.createAddresses(addressCount, user, enricher));
        return user;
    }

    public static User createUserWithAddresses(int addressCount) {
        return createUserWithAddresses(addressCount, NO_OP);
    }

    public static List<User> createUsersWithAddresses(int usersCount, int addressCount) {
        return createUsersWithAddresses(usersCount, addressCount, NO_OP);
    }

    public static List<User> createUsersWithAddresses(int usersCount, int addressCount,
                                                    BiConsumer<Integer, Address.AddressBuilder> enricher) {
        return IntStream.range(0, usersCount)
                .mapToObj(i -> createUserWithAddresses(addressCount, enricher))
                .collect(Collectors.toList());
    }
}
