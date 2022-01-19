package com.cloudbeds.userservice.domain;

import com.cloudbeds.userservice.utils.AddressFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void savingUserWithMultipleAddressesWorks() {
        // given
        User user = createUser();
        user.setAddresses(AddressFixtures.createAddresses(5, user,
                (i, address) -> address.addressLine1("address " + i)
        ));

        // when
        User saved = userRepository.save(user);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAddresses()).hasSize(5);
    }

    private User createUser() {
        String email = UUID.randomUUID() + "@skynet.com";
        return new User("John", "Connor", email, "password");
    }

    @Test
    void findsUsersByCountry() {
        // given
        List<String> countries = asList("Portugal", "Argentina", "Czech", "Belarus", "Japan");
        IntStream.range(0, 10)
                .forEach(i -> {
                    User user = createUser();
                    user.setAddresses(AddressFixtures.createAddresses((i % 5) + 1, user,
                            (ii, address) -> address.country(countries.get(ii))
                    ));
                    userRepository.save(user);
                });


        // when
        Iterable<User> japanUsers = userRepository.findByAddresses_Country("Japan");
        Iterable<User> belarusUsers = userRepository.findByAddresses_Country("Belarus");

        // then
        assertThat(japanUsers).hasSize(2);
        assertThat(belarusUsers).hasSize(4);
    }

    @Test
    void emailMustBeUnique() {
        // given
        User user1 = createUser();
        user1.setEmail("same-email@email.com");
        User user2 = createUser();
        user2.setEmail("same-email@email.com");

        // when
        userRepository.save(user1);
        Throwable throwable = catchThrowable(() -> userRepository.save(user2));

        // then
        assertThat(throwable).isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class)
                .hasMessageContaining("could not execute statement")
                .hasMessageContaining("insert into user");
    }
}