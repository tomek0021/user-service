# user-service

Java project using spring-boot and h2 in-memory database.
Build with gradle.

### Building with test

```shell
./gradlew build
```

### Running

```shell
./gradlew bootRun
```

### Local tests

#### Creating user with address
grpcurl --plaintext \
-d '{"firstName": "first", "lastName": "last", "email1": "em1", "password": "pass", "addresses": [{"addressLine1": "line1", "country": "Poland"}]}' \
localhost:9090 com.cloudbeds.userservice.UserService/CreateUser

#### Finding users per country
grpcurl --plaintext -d '{"country": "Poland"}' localhost:9090 com.cloudbeds.userservice.UserService/FindUsersFromCountry