package com.cloudbeds.userservice.testutils;


import com.cloudbeds.userservice.domain.Address;
import com.cloudbeds.userservice.domain.User;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Handy class for generating test objects. Follows https://en.wikipedia.org/wiki/Test_fixture.
 */
public class AddressFixtures {

    public static Address createAddress() {
        return defaultAddressBuilder().build();
    }

    private static Address.AddressBuilder defaultAddressBuilder() {
        return Address.builder()
                .addressLine1("address line 1")
                .addressLine2("address line 2")
                .state("Radom")
                .city("Pionki")
                .country("Poland")
                .zip("26-670");
    }

    public static List<Address> createAddresses(int count, User user, BiConsumer<Integer, Address.AddressBuilder> enricher) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Address.AddressBuilder addressBuilder = defaultAddressBuilder();
                    addressBuilder.user(user);
                    enricher.accept(i, addressBuilder);
                    return addressBuilder;
                })
                .map(Address.AddressBuilder::build)
                .collect(Collectors.toList());
    }

}
