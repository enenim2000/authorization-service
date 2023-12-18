package com.elara.authorizationservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Table(name = "NotificationCache")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCache implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Id
    private Long id;

    @Column(name = "token", unique = true)
    private String token;//SHA 256 Hash of companyCode,userId,notificationType,otp

    @Column(name = "companyCode")
    private String companyCode;

    @Column(name = "userId")
    private long userId;

    @Column(name = "notificationType")
    private String notificationType;

    @Column(name = "otp")
    private String otp;

    @Column(name = "expiry")
    private Date expiry;

}
