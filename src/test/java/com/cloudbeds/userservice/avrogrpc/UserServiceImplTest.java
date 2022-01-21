package com.cloudbeds.userservice.avrogrpc;

import com.cloudbeds.userservice.domain.UserRepository;
import com.cloudbeds.userservice.testutils.UserFixtures;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private UserServiceImpl userService = new UserServiceImpl(userRepository);

    @Test
    void callsRepositoryToGetUser() {
        // given
        com.cloudbeds.userservice.domain.User user = UserFixtures.createUserWithAddresses(1);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

        // when
        User avroUser = userService.getUser(user.getId().toString());

        // then
        assertThat(avroUser.getId()).isEqualTo(user.getId().toString());
        verify(userRepository).findById(user.getId());
    }

    @Test
    void getUserMapsUserWithAddress() {
        // given
        com.cloudbeds.userservice.domain.User user = UserFixtures.createUserWithAddresses(1);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

        // when
        User avroUser = userService.getUser(user.getId().toString());

        // then
        assertThat(avroUser.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(avroUser.getLastName()).isEqualTo(user.getLastName());
        assertThat(avroUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(avroUser.getPassword()).isEqualTo(user.getPassword());

        assertThat(avroUser.getAddresses()).hasSize(1);
        Address avroAddress = avroUser.getAddresses().get(0);
        com.cloudbeds.userservice.domain.Address address = user.getAddresses().get(0);
        assertThat(avroAddress.getAddressLine1()).isEqualTo(address.getAddressLine1());
        assertThat(avroAddress.getAddressLine2()).isEqualTo(address.getAddressLine2());
        assertThat(avroAddress.getCity()).isEqualTo(address.getCity());
        assertThat(avroAddress.getCountry()).isEqualTo(address.getCountry());
        assertThat(avroAddress.getState()).isEqualTo(address.getState());
        assertThat(avroAddress.getZip()).isEqualTo(address.getZip());
    }
}