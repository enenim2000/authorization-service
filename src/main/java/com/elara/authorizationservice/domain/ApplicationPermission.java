package com.elara.authorizationservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Table(name = "ApplicationPermission")
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationPermission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @Column(name = "applicationId")
    private long applicationId;

    @Column(name = "permissionId", unique = true)
    private String permissionId;

    @Column(name = "permission")
    private String permission; //The value of PreAuthorize CREATE_USER

    @Column(name = "description")
    private String description;

    @Column(name = "httpMethod")
    private String httpMethod;

    @Column(name = "uriPath")
    private String uriPath;

    @Column(name = "isSecured")
    private boolean isSecured;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "updatedAt")
    private Date updatedAt;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "updatedBy")
    private String updatedBy;
}
