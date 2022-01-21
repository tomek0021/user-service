package com.cloudbeds.userservice.domain;

import com.cloudbeds.userservice.testutils.UserFixtures;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cloudbeds.userservice.testutils.UserFixtures.createUser;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void savingUserWithMultipleAddressesWorks() {
        // given
        User user = UserFixtures.createUserWithAddresses(5,
                (i, address) -> address.addressLine1("address " + i)
        );

        // when
        User saved = userRepository.save(user);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAddresses()).hasSize(5);
    }

    @Test
    void findsUsersByCountry() {
        // given
        List<String> countries = asList("Portugal", "Argentina", "Czech", "Belarus", "Japan", "Canada");
        AtomicInteger counter = new AtomicInteger();
        UserFixtures.createUsersWithAddresses(10, 3,
                (i, a) -> a.country(countries.get(counter.getAndIncrement() % countries.size())))
                .forEach(userRepository::save);


        // when
        Iterable<User> japanUsers = userRepository.findByAddresses_Country("Japan");
        Iterable<User> belarusUsers = userRepository.findByAddresses_Country("Belarus");

        // then
        assertThat(japanUsers).hasSize(5);
        assertThat(belarusUsers).hasSize(5);
    }

    @Test
    @Disabled
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