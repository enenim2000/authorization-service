package com.elara.authorizationservice.repository;

import com.elara.authorizationservice.domain.ApplicationPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationPermissionRepository extends JpaRepository<ApplicationPermission, Long>, JpaSpecificationExecutor<ApplicationPermission> {

  ApplicationPermission findByPermissionId(String permissionId);

  ApplicationPermission findByApplicationId(long applicationId);
}
