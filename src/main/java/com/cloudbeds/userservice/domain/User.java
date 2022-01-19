package com.cloudbeds.userservice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class User {

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> addresses = new ArrayList<>();

    public void addAddress(Address address) {
        addresses.add(address);
    }
}
