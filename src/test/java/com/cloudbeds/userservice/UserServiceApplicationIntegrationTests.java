package com.cloudbeds.userservice;

import com.cloudbeds.userservice.avrogrpc.UserService;
import com.cloudbeds.userservice.protogrpc.CreateUserResponse;
import com.cloudbeds.userservice.protogrpc.FindUsersFromCountryRequest;
import com.cloudbeds.userservice.protogrpc.UserListResponse;
import com.cloudbeds.userservice.protogrpc.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.avro.grpc.AvroGrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static com.cloudbeds.userservice.testutils.CreateUserRequestFixtures.createCreateUserRequest;
import static com.cloudbeds.userservice.testutils.CreateUserRequestFixtures.createUserRequestWithAddress;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
		"grpc.server.inProcessName=test", // Enable inProcess server
		"grpc.server.port=-1", // Disable external server
		"grpc.client.inProcess.address=in-process:test" // Configure the client to connect to the inProcess server
})
@DirtiesContext
class UserServiceApplicationIntegrationTests {

	@GrpcClient("inProcess")
	private UserServiceGrpc.UserServiceBlockingStub userServiceClient;

	@Value("${user-service.avro-grpc.port}")
	private int avroGrpcPort;

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
				.extracting(com.cloudbeds.userservice.protogrpc.User::getId).containsExactly(user2.getId());
	}


	@Test
	void testAvroGrpc() {
		// given
		UserService stub = createAvroGrpcClient();
		CreateUserResponse user = userServiceClient.createUser(createCreateUserRequest());

		// when
		com.cloudbeds.userservice.avrogrpc.User avroUser = stub.getUser(user.getId());

		// then
		assertThat(avroUser.getId()).isEqualTo(user.getId());
	}

	private UserService createAvroGrpcClient() {
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", avroGrpcPort).usePlaintext().build();
		return AvroGrpcClient.create(channel, UserService.class);
	}

}
