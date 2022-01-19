package com.cloudbeds.userservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Address {

    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zip;
    private String country;
}
