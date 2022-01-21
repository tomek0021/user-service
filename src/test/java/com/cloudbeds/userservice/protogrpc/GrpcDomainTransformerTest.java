package com.cloudbeds.userservice.protogrpc;

import com.cloudbeds.userservice.domain.Address;
import com.cloudbeds.userservice.domain.User;
import com.cloudbeds.userservice.testutils.UserFixtures;
import org.junit.jupiter.api.Test;

import static com.cloudbeds.userservice.testutils.CreateUserRequestFixtures.createCreateUserRequest;
import static org.assertj.core.api.Assertions.assertThat;

class GrpcDomainTransformerTest {

    private GrpcDomainTransformer transformer = new GrpcDomainTransformer();

    @Test
    void createUserMapsCreateUserRequestIntoDomainUser() {
        // given
        CreateUserRequest request = createCreateUserRequest();

        // when
        User user = transformer.mapCreateUserRequestToDomainUser(request);

        // then
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

    @Test
    void findUsersFromCountryMapProperValues() {
        // given
        User user = UserFixtures.createUserWithAddresses(1);

        // when
        com.cloudbeds.userservice.protogrpc.User grpcUser = transformer.mapUser(user);

        // then
        assertThat(grpcUser.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(grpcUser.getAddressesList().get(0).getCountry()).isEqualTo(user.getAddresses().get(0).getCountry());
        // TODO asserions
    }
}