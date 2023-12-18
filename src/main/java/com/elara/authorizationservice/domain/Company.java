package com.elara.authorizationservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Table(name = "Company")
@Entity
@Getter
@Setter
public class Company implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @Column(name = "companyName", unique = true)
    private String companyName;

    @Column(name = "companyCode", unique = true)
    private String companyCode;

    @Column(name = "companyAddress")
    private String companyAddress;

    @Column(name = "clientId", length = 4000, unique = true)
    private String clientId;

    @Column(name = "clientSecret", length = 4000)
    private String clientSecret;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "createdAt", nullable = false)
    private Date createdAt;

    @Column(name = "updatedAt")
    private Date updatedAt;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "updatedBy")
    private String updatedBy;
}
