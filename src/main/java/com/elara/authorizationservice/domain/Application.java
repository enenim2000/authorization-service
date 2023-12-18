package com.elara.authorizationservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Table(name = "Application")
@Entity
@Getter
@Setter
public class Application implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @Column(name = "appName", unique = true)
    private String appName;

    @Column(name = "appServer")
    private String appServer;

    @Column(name = "appServerPort")
    private String appServerPort;

    @Column(name = "publicKey", length = 4000, unique = true)
    private String publicKey;

    @Column(name = "privateKey", length = 4000)
    private String privateKey;

    @Column(name = "createdAt", nullable = false)
    private Date createdAt;

    @Column(name = "updatedAt")
    private Date updatedAt;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "updatedBy")
    private String updatedBy;

    @Column(name = "status", nullable = false)
    private String status;
}
