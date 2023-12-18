package com.elara.authorizationservice.repository;

import com.elara.authorizationservice.domain.UserGroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserGroupPermissionRepository extends JpaRepository<UserGroupPermission, Long>, JpaSpecificationExecutor<UserGroupPermission> {

  List<UserGroupPermission> findByGroupIdIn(List<Long> groupIds);

    List<UserGroupPermission> findByGroupIdAndCompanyCode(Long groupId, String companyCode);

  UserGroupPermission findByCompanyCodeAndGroupIdAndApplicationPermissionId(String companyCode, long groupId, Long applicationPermissionId);

  @Modifying
  @Query("delete from UserGroupPermission ugp where ugp.companyCode = :companyCode and ugp.groupId = :groupId and ugp.applicationPermissionId in :permissionIds")
  void deleteByCompanyCodeAndGroupIdAndApplicationPermissionIdIn(@Param("companyCode") String companyCode, @Param("groupId") Long groupId,  @Param("permissionIds")List<Long> permissionIds);
}
