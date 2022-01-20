# user-service

Java project using spring-boot and h2 in-memory database. User spring-data-jpa for handling data part. For rest used I've
used spring-data-rest - I know it break encapsulation a lot but didn't want to spend too much time on this in fact unneseccary
layer for this project.

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

#### REST 

##### Creating user with address
curl -i -X POST -H "Content-Type:application/json" -d '{  "firstName": "first", "lastName": "last", "email2": "em1", "password": "pass", "addresses": [{"addressLine1": "line1", "country": "Poland"}]}}' \
http://localhost:8080/users

##### Finding users per country
http://localhost:8080/users/search/findByAddresses_Country?country=Poland


#### gRPC

##### Creating user with address
grpcurl --plaintext \
-d '{"firstName": "first", "lastName": "last", "email1": "em1", "password": "pass", "addresses": [{"addressLine1": "line1", "country": "Poland"}]}' \
localhost:9090 com.cloudbeds.userservice.UserService/CreateUser

##### Finding users per country
grpcurl --plaintext -d '{"country": "Poland"}' localhost:9090 com.cloudbeds.userservice.UserService/FindUsersFromCountry
