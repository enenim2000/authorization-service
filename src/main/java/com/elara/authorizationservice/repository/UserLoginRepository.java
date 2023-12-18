package com.elara.authorizationservice.repository;

import com.elara.authorizationservice.domain.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, Long>, JpaSpecificationExecutor<UserLogin> {

  UserLogin findByUserId(long userId);

  UserLogin findByUuid(String uuid);

  UserLogin findByUserIdAndAccessToken(long userId, String accessToken);

  UserLogin findByCompanyCodeAndUserId(String companyCode, long userId);

  UserLogin findByUserIdAndRefreshToken(long userId, String refreshToken);
}
