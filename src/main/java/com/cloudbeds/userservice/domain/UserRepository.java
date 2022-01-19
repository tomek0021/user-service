package com.cloudbeds.userservice.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {

    Iterable<User> findByAddresses_Country(String country);
}
