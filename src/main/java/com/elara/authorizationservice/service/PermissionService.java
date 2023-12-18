package com.elara.authorizationservice.service;

import com.elara.authorizationservice.auth.Permission;
import com.elara.authorizationservice.auth.RequestUtil;
import com.elara.authorizationservice.domain.*;
import com.elara.authorizationservice.dto.request.*;
import com.elara.authorizationservice.dto.response.*;
import com.elara.authorizationservice.enums.EntityStatus;
import com.elara.authorizationservice.exception.AppException;
import com.elara.authorizationservice.repository.*;
import com.elara.authorizationservice.util.HashUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@Slf4j
@Service
public class PermissionService {

  final ModelMapper modelMapper;
  final MessageService messageService;
  private final ApplicationRepository applicationRepository;
  private final GroupRepository groupRepository;
  private final UserGroupRepository userGroupRepository;
  private final UserGroupPermissionRepository userGroupPermissionRepository;
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final ApplicationAccountRepository applicationAccountRepository;
  private final ApplicationPermissionRepository applicationPermissionRepository;

  @Value("${spring.application.name}")
  private String appName;

  public PermissionService(
          ApplicationRepository applicationRepository,
          ModelMapper modelMapper,
          MessageService messageService,
          GroupRepository groupRepository,
          UserGroupRepository userGroupRepository,
          UserGroupPermissionRepository userGroupPermissionRepository,
          CompanyRepository companyRepository,
          UserRepository userRepository,
          ApplicationAccountRepository applicationAccountRepository,
          ApplicationPermissionRepository applicationPermissionRepository) {
    this.applicationRepository = applicationRepository;
    this.modelMapper = modelMapper;
    this.messageService = messageService;
    this.groupRepository = groupRepository;
    this.userGroupRepository = userGroupRepository;
    this.userGroupPermissionRepository = userGroupPermissionRepository;
    this.companyRepository = companyRepository;
    this.userRepository = userRepository;
    this.applicationAccountRepository = applicationAccountRepository;
    this.applicationPermissionRepository = applicationPermissionRepository;
  }


  public CreateGroupResponse createGroup(CreateGroupRequest dto) {
    Company company = companyRepository.findByCompanyCode(dto.getCompanyCode());
    if (company == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    Group existing = groupRepository.findByGroupNameAndCompanyCode(dto.getGroupName(), company.getCompanyCode());
    if (existing != null) {
      throw new AppException(messageService.getMessage("Group.Exist"));
    }

    Group newEntry = modelMapper.map(dto, Group.class);
    newEntry.setCreatedBy(RequestUtil.getAuthToken().getUsername());
    newEntry.setCreatedAt(new Date());
    newEntry.setStatus(EntityStatus.Enabled.name());
    newEntry.setCompanyCode(company.getCompanyCode());
    newEntry = groupRepository.save(newEntry);
    CreateGroupResponse response = new CreateGroupResponse();
    response.setData(modelMapper.map(newEntry, CreateGroupResponse.Data.class));
    return response;
  }

  public UpdateGroupResponse updateGroup(UpdateGroupRequest dto) {
    Company company = companyRepository.findByCompanyCode(dto.getCompanyCode());
    if (company == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    Group existing = groupRepository.findByGroupNameAndCompanyCode(dto.getGroupName(), company.getCompanyCode());
    if (existing == null) {
      throw new AppException(messageService.getMessage("Group.NotFound"));
    }

    modelMapper.map(dto, existing);
    existing.setUpdatedBy(RequestUtil.getAuthToken().getUsername());
    existing.setUpdatedAt(new Date());
    existing.setCompanyCode(company.getCompanyCode());
    existing = groupRepository.save(existing);
    UpdateGroupResponse response = new UpdateGroupResponse();
    response.setData(modelMapper.map(existing, UpdateGroupResponse.Data.class));
    return response;
  }

  public AssignUserGroupResponse assignGroupToUser(AssignUserGroupRequest dto) {
    Company existing = companyRepository.findByCompanyName(dto.getCompanyCode());
    if (existing == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    Optional<User> optionalUser = userRepository.findById(dto.getUserId());
    if (optionalUser.isEmpty()) {
      throw new AppException(messageService.getMessage("User.NotFound"));
    }

    User user = optionalUser.get();

    List<Long> deleteGroupIds = new ArrayList<>();

    List<UserGroup> userGroups = userGroupRepository.findByUserIdAndCompanyCode(user.getId(), dto.getCompanyCode());
    for (UserGroup userGroup : userGroups) {
      if (!dto.getGroupIds().contains(userGroup.getGroupId())) {
        deleteGroupIds.add(userGroup.getGroupId());
      }
    }

    for (Long groupId : dto.getGroupIds()) {
      Optional<Group> optionalGroup = groupRepository.findById(groupId);
      if (optionalGroup.isPresent()) {
        UserGroup existingUserGroup = userGroupRepository.findByCompanyCodeAndUserIdAndGroupId(dto.getCompanyCode(), dto.getUserId(), groupId);
        if (existingUserGroup == null) {
          userGroupRepository.save(UserGroup.builder()
                  .groupId(groupId)
                  .companyCode(dto.getCompanyCode())
                  .userId(user.getId())
                  .createdAt(new Date())
                  .createdBy(RequestUtil.getAuthToken().getUsername())
                  .build());
        }
      }
    }

    userGroupRepository.deleteByCompanyCodeAndUserIdAndGroupIdIn(dto.getCompanyCode(), user.getId(), deleteGroupIds);

    return new AssignUserGroupResponse();
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public AssignUserPermissionResponse assignPermissionToUser(AssignUserPermissionRequest dto) {
    Company existing = companyRepository.findByCompanyCode(dto.getCompanyCode());
    if (existing == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    Optional<User> optionalUser = userRepository.findById(dto.getUserId());
    if (optionalUser.isEmpty()) {
      throw new AppException(messageService.getMessage("User.NotFound"));
    }

    User user = optionalUser.get();

    List<String> deletePermissionIds = new ArrayList<>();

    List<ApplicationAccount> userPermissions = applicationAccountRepository.findByUserIdAndCompanyCode(user.getId(), dto.getCompanyCode());
    for (ApplicationAccount userPermission : userPermissions) {
      if (!dto.getPermissionIds().contains(userPermission.getPermissionId())) {
        deletePermissionIds.add(userPermission.getPermissionId());
      }
    }

    for (String permissionId : dto.getPermissionIds()) {
      ApplicationPermission applicationPermission = applicationPermissionRepository.findByPermissionId(permissionId);
      if (applicationPermission != null) {
        ApplicationAccount applicationAccount = applicationAccountRepository.findByCompanyCodeAndUserIdAndPermissionId(dto.getCompanyCode(), dto.getUserId(), permissionId);
        if (applicationAccount == null) {
          applicationAccountRepository.save(ApplicationAccount.builder()
                  .permissionId(permissionId)
                  .companyCode(dto.getCompanyCode())
                  .userId(user.getId())
                  .createdAt(new Date())
                  .createdBy(RequestUtil.getAuthToken().getUsername())
                  .status(EntityStatus.Enabled.name())
                  .build());
        }
      }
    }

    applicationAccountRepository.deleteByCompanyCodeAndUserIdAndPermissionIdIn(dto.getCompanyCode(), user.getId(), deletePermissionIds);

    return new AssignUserPermissionResponse();
  }

  public SyncPermissionResponse syncApplicationPermission(SyncPermissionRequest dto) {
    Application application = applicationRepository.findByAppName(dto.getAppName());
    if (application == null) {
      throw new AppException(messageService.getMessage("App.NotFound"));
    }

    for (SyncPermissionRequest.Data data : dto.getPermissions()) {
      String permissionId = HashUtil.getHash(dto.getAppName() + data.getPermission());
      ApplicationPermission applicationPermission = applicationPermissionRepository.findByPermissionId(permissionId);
      if (applicationPermission == null) {
        applicationPermissionRepository.save(ApplicationPermission.builder()
                        .applicationId(application.getId())
                        .permissionId(permissionId)
                        .description(data.getDescription())
                        .isSecured(data.isSecured())
                        .httpMethod(data.getHttpMethod())
                        .permission(data.getPermission())
                        .uriPath(data.getUriPath())
                        .status(EntityStatus.Enabled.name())
                        .createdAt(new Date())
                        .createdBy(dto.getAppName())
                        .build());
      } else {
        applicationPermission.setPermission(data.getPermission());
        applicationPermission.setDescription(data.getDescription());
        applicationPermission.setSecured(data.isSecured());
        applicationPermission.setUpdatedAt(new Date());
        applicationPermission.setUpdatedBy(dto.getAppName());
        applicationPermissionRepository.save(applicationPermission);
      }
    }

    return new SyncPermissionResponse();
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public AssignGroupPermissionResponse assignPermissionToGroup(AssignGroupPermissionRequest dto) {
    Company existing = companyRepository.findByCompanyCode(dto.getCompanyCode());
    if (existing == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    Optional<Group> optionalGroup = groupRepository.findById(dto.getGroupId());
    if (optionalGroup.isEmpty()) {
      throw new AppException(messageService.getMessage("Group.NotFound"));
    }

    Group group = optionalGroup.get();

    List<Long> deletePermissionIds = new ArrayList<>();

    List<UserGroupPermission> groupPermissions = userGroupPermissionRepository.findByGroupIdAndCompanyCode(group.getId(), dto.getCompanyCode());
    for (UserGroupPermission groupPermission : groupPermissions) {
      if (!dto.getPermissionIds().contains(groupPermission.getApplicationPermissionId())) {
        deletePermissionIds.add(groupPermission.getApplicationPermissionId());
      }
    }

    for (Long applicationPermissionId : dto.getPermissionIds()) {
      Optional<ApplicationPermission> applicationPermission = applicationPermissionRepository.findById(applicationPermissionId);
      if (applicationPermission.isPresent()) {
        UserGroupPermission userGroupPermission = userGroupPermissionRepository.findByCompanyCodeAndGroupIdAndApplicationPermissionId(dto.getCompanyCode(), dto.getGroupId(), applicationPermissionId);
        if (userGroupPermission == null) {
          userGroupPermissionRepository.save(UserGroupPermission.builder()
                  .companyCode(dto.getCompanyCode())
                  .groupId(group.getId())
                  .applicationPermissionId(applicationPermissionId)
                  .createdAt(new Date())
                  .createdBy(RequestUtil.getAuthToken().getUsername())
                  .status(EntityStatus.Enabled.name())
                  .build());
        }
      }
    }

    userGroupPermissionRepository.deleteByCompanyCodeAndGroupIdAndApplicationPermissionIdIn(dto.getCompanyCode(), group.getId(), deletePermissionIds);

    return new AssignGroupPermissionResponse();
  }

  public CreatePermissionResponse createApplicationPermission(CreatePermissionRequest dto) {

    Application application = applicationRepository.findByAppName(dto.getAppName());
    if (application == null) {
      throw new AppException(messageService.getMessage("App.NotFound"));
    }

    for (CreatePermissionRequest.Data data : dto.getPermissions()) {
      String permissionId = HashUtil.getHash(application.getAppName() + data.getPermission());
      ApplicationPermission applicationPermission = applicationPermissionRepository.findByPermissionId(permissionId);
      if (applicationPermission == null) {
        applicationPermission = new ApplicationPermission();
        applicationPermission.setApplicationId(application.getId());
        applicationPermission.setPermissionId(permissionId);
        applicationPermission.setCreatedBy(RequestUtil.getAuthToken().getUsername());
        applicationPermission.setPermission(data.getPermission());
        applicationPermission.setSecured(true);
        applicationPermission.setCreatedAt(new Date());
        applicationPermission.setStatus(EntityStatus.Enabled.name());
        applicationPermission.setDescription(data.getDescription());
        applicationPermission.setHttpMethod(data.getHttpMethod());
        applicationPermission.setUriPath(data.getUriPath());
        applicationPermissionRepository.save(applicationPermission);
      }
    }

    return new CreatePermissionResponse();
  }

  public SyncPermissionRequest generatePermissionRequest(RequestMappingHandlerMapping requestMappingHandlerMapping) {
    Map<RequestMappingInfo, HandlerMethod> endpoints = requestMappingHandlerMapping.getHandlerMethods();
    Iterator<Map.Entry<RequestMappingInfo, HandlerMethod>> it = endpoints.entrySet().iterator();
    HandlerMethod handlerMethod;
    RequestMappingInfo requestInfo;
    String permission;
    SyncPermissionRequest syncPermissionRequest = new SyncPermissionRequest();
    syncPermissionRequest.setAppName(appName);
    syncPermissionRequest.setPermissions(new ArrayList<>());

    while (it.hasNext()) {
      Map.Entry<RequestMappingInfo, HandlerMethod> pair = it.next();
      handlerMethod = pair.getValue();
      requestInfo = pair.getKey();
      if(handlerMethod.hasMethodAnnotation(Permission.class)) {
        permission = handlerMethod.getMethod().getDeclaredAnnotation(Permission.class).value();
        Optional<String> pathUri = requestInfo.getDirectPaths().stream().findFirst();

        String method = HttpMethod.POST.name();
        if (handlerMethod.getMethod().isAnnotationPresent(GetMapping.class)) {
          method = HttpMethod.GET.name();
        } else if (handlerMethod.getMethod().isAnnotationPresent(PutMapping.class)) {
          method = HttpMethod.PUT.name();
        } else if (handlerMethod.getMethod().isAnnotationPresent(PatchMapping.class)) {
          method = HttpMethod.PATCH.name();
        } else if (handlerMethod.getMethod().isAnnotationPresent(DeleteMapping.class)) {
          method = HttpMethod.DELETE.name();
        }

        syncPermissionRequest.getPermissions().add(
                SyncPermissionRequest.Data.builder()
                        .permission(permission)
                        .description(handlerMethod.getMethod().getDeclaredAnnotation(Operation.class).summary())
                        .httpMethod(method)
                        .isSecured(true)
                        .uriPath(pathUri.orElse(""))
                        .build());
      }
    }

    return syncPermissionRequest;
  }
}
