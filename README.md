# user-service


grpcurl --plaintext -d '{"firstName": "first", "lastName": "last", "email": "em", "password": "pass", "addresses": [{"addressLine1": "line1"}]}' localhost:9090 com.cloudbeds.userservice.UserService/CreateUser