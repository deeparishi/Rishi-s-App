package com.jwt.JwtSecurity.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_address")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "user")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "house_number")
    Long houseNumber;

    @Column(name = "street_name")
    String streetName;

    @Column(name = "zip_code")
    Long zipCode;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_login_id", referencedColumnName=  "login_id")
    @JsonBackReference(value = "user-address-reference")
    User user;

}
