# user-service

Java project using spring-boot and h2 in-memory database. User spring-data-jpa for handling data part. For rest I've
used spring-data-rest - I know it break encapsulation a lot but didn't want to spend too much time on this, and in fact it
would be unneseccary layer for a project like this.

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
curl "http://localhost:8080/users/search/findByAddresses_Country?country=Poland"


#### gRPC with proto

##### Creating user with address
grpcurl --plaintext \
-d '{"firstName": "first", "lastName": "last", "email1": "em1", "password": "pass", "addresses": [{"addressLine1": "line1", "country": "Poland"}]}' \
localhost:9090 com.cloudbeds.userservice.UserService/CreateUser

##### Finding users per country
grpcurl --plaintext -d '{"country": "Poland"}' localhost:9090 com.cloudbeds.userservice.UserService/FindUsersFromCountry


#### gRPC with proto

##### Finding users per id (does NOT work: 'server does not support the reflection API' - couldn't find ReflectionService for avro) 
grpcurl --plaintext -d '{"id": "0ef5d2f9-d203-4be5-b9f3-6cbac696f967"}' localhost:9091 com.cloudbeds.userservice.avrogrpc.UserService/getUser
