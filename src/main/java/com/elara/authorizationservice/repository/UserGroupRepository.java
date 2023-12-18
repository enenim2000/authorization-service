package com.elara.authorizationservice.repository;

import com.elara.authorizationservice.domain.UserGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserGroupRepository extends JpaRepository<UserGroup, Long>, JpaSpecificationExecutor<UserGroup> {
  List<UserGroup> findByUserId(long userId);

  @Modifying
  @Query("delete from UserGroup ug where ug.companyCode = :companyCode  and ug.userId = :userId and ug.groupId in :groupIds")
  void deleteByCompanyCodeAndUserIdAndGroupIdIn(@Param("companyCode") String companyCode, @Param("userId")Long userId, @Param("groupIds")List<Long> groupIds);

  List<UserGroup> findByUserIdAndCompanyCode(long userId, String companyCode);

  UserGroup findByCompanyCodeAndUserIdAndGroupId(String companyCode, long userId, long groupId);
}
