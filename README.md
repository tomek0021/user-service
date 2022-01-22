# user-service

Java project using spring-boot and h2 in-memory database. User spring-data-jpa for handling data part. For rest I've
used spring-data-rest - I know it breaks encapsulation a lot but didn't want to spend too much time on this, and in fact it
would be unneseccary layer for a project like this.
I've never used grpc before so I've first did creating/finding with spring-boot starter for grpc (with proto) I've found 
to cover hard thing first. That's why I've extended requirements a bit.
I've spend quite some time on finding solution for having avro and found only bare (no spring) solution on avro github - 
which I've springified myself.
In the end there are 3 public enpoints:
* rest (port 8080)
* grpc with proto (port 9090)
* grpc with avro (port 9091)

Project is build with gradle.

I've covered most of the code with junits and did integration tests with spring runner. Next I'd add e2e tests with docker.



### Building with test

```shell
./gradlew build
```


### Running

```shell
./gradlew bootRun
```


### Local tests


#### *REST* 

##### Creating user with address
```shell
curl -i -X POST -H "Content-Type:application/json" -d '{  "firstName": "first", "lastName": "last", "email": "em1", "password": "pass", "addresses": [{"addressLine1": "line1", "country": "Poland"}]}}' \
http://localhost:8080/users
```

##### Finding users per country
```shell
curl "http://localhost:8080/users/search/byCountry?country=Poland"
```


#### *gRPC with proto*

##### Creating user with address
```shell
grpcurl --plaintext \
-d '{"firstName": "first", "lastName": "last", "email": "em1", "password": "pass", "addresses": [{"addressLine1": "line1", "country": "Poland"}]}' \
localhost:9090 com.cloudbeds.userservice.UserService/CreateUser
```

##### Finding users per country
```shell
grpcurl --plaintext -d '{"country": "Poland"}' localhost:9090 com.cloudbeds.userservice.UserService/FindUsersFromCountry
```


#### *gRPC with avro*

##### Finding users per id 
**!!! does NOT work: 'server does not support the reflection API' - couldn't find ReflectionService for avro !!!**
```shell 
grpcurl --plaintext -d '{"id": "0ef5d2f9-d203-4be5-b9f3-6cbac696f967"}' localhost:9091 com.cloudbeds.userservice.avrogrpc.UserService/getUser
```
