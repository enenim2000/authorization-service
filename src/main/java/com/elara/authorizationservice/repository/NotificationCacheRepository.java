package com.elara.authorizationservice.repository;

import com.elara.authorizationservice.domain.NotificationCache;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
@Transactional
public interface NotificationCacheRepository extends JpaRepository<NotificationCache, Long>, JpaSpecificationExecutor<NotificationCache> {

  NotificationCache findByToken(String token);

  @Modifying
  @Query("DELETE FROM NotificationCache n WHERE n.expiry <= :now")
  void deleteExpiredOtp(@Param("now") Date now);

  @Modifying
  @Query("DELETE FROM NotificationCache n WHERE n.token = :token")
  void deleteUsedOtp(@Param("token") String token);
}
