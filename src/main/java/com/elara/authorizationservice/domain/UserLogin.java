package com.elara.authorizationservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "UserLogin")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin implements Serializable {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @Column(name = "companyCode")
    private String companyCode;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "userId")
    private long userId;

    @Column(name = "password", length = 3000)
    private String password;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "updatedAt")
    private String updatedAt;

    @Column(name = "accessToken", length = 4000, unique = true)
    private String accessToken;

    @Column(name = "refreshToken", length = 4000, unique = true)
    private String refreshToken;

}
