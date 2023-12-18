package com.elara.authorizationservice.repository;

import com.elara.authorizationservice.domain.ApplicationAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationAccountRepository extends JpaRepository<ApplicationAccount, Long>, JpaSpecificationExecutor<ApplicationAccount> {

  public List<ApplicationAccount> findByUserId(Long userId);

  List<ApplicationAccount> findByUserIdAndCompanyCode(long userId, String companyCode);

  ApplicationAccount findByCompanyCodeAndUserIdAndPermissionId(String companyCode, long userId, String permissionId);

  @Modifying
  @Query("delete from ApplicationAccount ac where ac.companyCode = :companyCode and ac.userId = :userId and ac.permissionId in :permissionIds")
  void deleteByCompanyCodeAndUserIdAndPermissionIdIn(@Param("companyCode") String companyCode, @Param("userId") Long userId, @Param("permissionIds") List<String> permissionIds);
}
