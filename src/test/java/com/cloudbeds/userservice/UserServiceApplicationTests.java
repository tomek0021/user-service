package com.cloudbeds.userservice;

import com.cloudbeds.userservice.grpc.CreateUserResponse;
import com.cloudbeds.userservice.grpc.FindUsersFromCountryRequest;
import com.cloudbeds.userservice.grpc.UserListResponse;
import com.cloudbeds.userservice.grpc.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static com.cloudbeds.userservice.testutils.CreateUserRequestFixtures.createUserRequestWithAddress;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
		"grpc.server.inProcessName=test", // Enable inProcess server
		"grpc.server.port=-1", // Disable external server
		"grpc.client.inProcess.address=in-process:test" // Configure the client to connect to the inProcess server
})
@DirtiesContext
class UserServiceApplicationTests {

	@GrpcClient("inProcess")
	private UserServiceGrpc.UserServiceBlockingStub userServiceClient;

	@Test
	void testGrpcCreateAndThenFindByCountryUserE2e() throws InterruptedException {
		// given
		CreateUserResponse user1 = userServiceClient.createUser(createUserRequestWithAddress(
				addressBuilder -> addressBuilder.setCountry("Poland")));
		CreateUserResponse user2 = userServiceClient.createUser(createUserRequestWithAddress(
				addressBuilder -> addressBuilder.setCountry("Czech")));
		CreateUserResponse user3 = userServiceClient.createUser(createUserRequestWithAddress(
				addressBuilder -> addressBuilder.setCountry("Slovakia")));

		// when
		UserListResponse response = userServiceClient.findUsersFromCountry(FindUsersFromCountryRequest.newBuilder()
				.setCountry("Czech")
				.build());

		// then
		assertThat(response.getUsersList()).hasSize(1)
				.extracting(com.cloudbeds.userservice.grpc.User::getId).containsExactly(user2.getId());
	}

}
