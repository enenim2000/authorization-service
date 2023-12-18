package com.elara.authorizationservice.service;

import com.elara.authorizationservice.auth.RequestUtil;
import com.elara.authorizationservice.domain.Application;
import com.elara.authorizationservice.domain.ApplicationPermission;
import com.elara.authorizationservice.dto.request.CreateApplicationRequest;
import com.elara.authorizationservice.dto.request.UpdateApplicationRequest;
import com.elara.authorizationservice.dto.response.CreateApplicationResponse;
import com.elara.authorizationservice.dto.response.UpdateApplicationResponse;
import com.elara.authorizationservice.enums.EntityStatus;
import com.elara.authorizationservice.exception.AppException;
import com.elara.authorizationservice.repository.ApplicationPermissionRepository;
import com.elara.authorizationservice.repository.ApplicationRepository;
import com.elara.authorizationservice.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.*;

@Slf4j
@Service
public class ApplicationService {

  @PersistenceContext
  private final EntityManager entityManager;

  @Value("${app.public-key}")
  String publicKey;

  final ModelMapper modelMapper;
  final MessageService messageService;
  private final UserGroupService userGroupService;
  private final ApplicationRepository applicationRepository;
  private final ApplicationPermissionRepository applicationPermissionRepository;

  public ApplicationService(EntityManager entityManager,
                            UserGroupService userGroupService,
                            ApplicationRepository applicationRepository,
                            ModelMapper modelMapper,
                            MessageService messageService,
                            ApplicationPermissionRepository applicationPermissionRepository) {

    this.entityManager = entityManager;
    this.userGroupService = userGroupService;
    this.applicationRepository = applicationRepository;
    this.modelMapper = modelMapper;
    this.messageService = messageService;
    this.applicationPermissionRepository = applicationPermissionRepository;
  }

  public List<String> getAudience(long userId) {
    List<String> audience = new ArrayList<>();
    try {
      List<Long> groupIds = userGroupService.groupIds(userId);
      String groupIdString = StringUtils.join(groupIds, ',');
      TypedQuery<Object[]> query = entityManager.createQuery(
          "Select a.appName from UserGroupPermission ugp inner join ApplicationPermission ap on ugp.applicationPermissionId = ap.id inner join Application a on ap.applicationId = a.id where ugp.groupId in (" + groupIdString + ") group by a.appName", Object[].class);
      List<Object[]> results = query.getResultList();
      log.info("result.size(): {}", results.size());
      for (Object[] array : results) {
        audience.add((String)array[0]);
      }
      return audience;
    } catch (Exception e) {
      log.error("Error while attempting to get audience", e);
    }

    return audience;
  }

  public ApplicationPermission getByPermissionId(String permissionId) {
    return applicationPermissionRepository.findByPermissionId(permissionId);
  }

  public Application getByPublicKey(String serviceClientId) {
    return applicationRepository.findByPublicKey(serviceClientId);
  }

  public CreateApplicationResponse createApplication(CreateApplicationRequest dto) {
    Application existing = applicationRepository.findByAppName(dto.getAppName());

    if (existing != null) {
      throw new AppException(messageService.getMessage("App.Exist"));
    }

    Application newEntry = modelMapper.map(dto, Application.class);
    newEntry.setCreatedBy(RequestUtil.getAuthToken().getUsername());
    newEntry.setCreatedAt(new Date());
    newEntry.setStatus(EntityStatus.Enabled.name());

    newEntry.setPublicKey(RSAUtil.encrypt(UUID.randomUUID().toString(), publicKey));
    newEntry.setPrivateKey(RSAUtil.encrypt(UUID.randomUUID().toString(), publicKey));
    newEntry = applicationRepository.save(newEntry);

    CreateApplicationResponse response = new CreateApplicationResponse();
    response.setData(modelMapper.map(newEntry, CreateApplicationResponse.Data.class));
    return response;
  }

  public UpdateApplicationResponse updateApplication(UpdateApplicationRequest dto) {
    Application existing = applicationRepository.findByAppName(dto.getAppName());
    if (existing == null) {
      throw new AppException(messageService.getMessage("App.NotFound"));
    }

    modelMapper.map(dto, existing);
    existing.setUpdatedAt(new Date());
    existing.setUpdatedBy(RequestUtil.getAuthToken().getUsername());
    existing = applicationRepository.save(existing);
    UpdateApplicationResponse response = new UpdateApplicationResponse();
    response.setData(modelMapper.map(existing, UpdateApplicationResponse.Data.class));
    return response;
  }
}
