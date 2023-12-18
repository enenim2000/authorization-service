package com.elara.authorizationservice.service;

import com.elara.authorizationservice.auth.RequestUtil;
import com.elara.authorizationservice.domain.Company;
import com.elara.authorizationservice.domain.User;
import com.elara.authorizationservice.dto.request.CreateUserRequest;
import com.elara.authorizationservice.dto.request.UpdateUserRequest;
import com.elara.authorizationservice.dto.response.CreateUserResponse;
import com.elara.authorizationservice.dto.response.UpdateUserResponse;
import com.elara.authorizationservice.enums.EntityStatus;
import com.elara.authorizationservice.exception.AppException;
import com.elara.authorizationservice.repository.CompanyRepository;
import com.elara.authorizationservice.repository.UserRepository;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

  final UserRepository userRepository;
  final CompanyRepository companyRepository;
  final ModelMapper modelMapper;
  final MessageService messageService;

  public UserService(UserRepository userRepository,
      CompanyRepository companyRepository,
      ModelMapper modelMapper,
      MessageService messageService) {
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.modelMapper = modelMapper;
    this.messageService = messageService;
  }

  public CreateUserResponse createUser(CreateUserRequest dto) {

    Company company = companyRepository.findByCompanyCode(dto.getCompanyCode());
    if (company == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    User existing = userRepository.findByUsername(dto.getEmail());
    if (existing != null) {
      throw new AppException(messageService.getMessage("User.Exist"));
    }

    existing = userRepository.findByUsername(dto.getPhone());
    if (existing != null) {
      throw new AppException(messageService.getMessage("User.Exist"));
    }

    User newEntry = modelMapper.map(dto, User.class);
    newEntry.setCreatedBy(RequestUtil.getAuthToken().getUsername());
    newEntry.setCreatedAt(new Date());
    newEntry.setCompanyCode(company.getCompanyCode());
    newEntry.setStatus(EntityStatus.Enabled.name());
    newEntry = userRepository.save(newEntry);
    CreateUserResponse response = new CreateUserResponse();
    response.setData(modelMapper.map(newEntry, CreateUserResponse.Data.class));
    return response;
  }

  public UpdateUserResponse updateUser(UpdateUserRequest dto) {
    User existing = userRepository.findByUsername(dto.getEmail());
    if (existing == null) {
      throw new AppException(messageService.getMessage("User.NotFound"));
    }

    modelMapper.map(dto, existing);
    existing.setUpdatedAt(new Date());
    existing.setUpdatedBy(RequestUtil.getAuthToken().getUsername());
    existing = userRepository.save(existing);
    UpdateUserResponse response = new UpdateUserResponse();
    response.setData(modelMapper.map(existing, UpdateUserResponse.Data.class));
    return response;
  }

}
