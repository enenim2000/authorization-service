package com.elara.authorizationservice.service;

import com.elara.authorizationservice.auth.AuthToken;
import com.elara.authorizationservice.auth.RequestUtil;
import com.elara.authorizationservice.dto.request.*;
import com.elara.authorizationservice.dto.response.*;
import com.elara.authorizationservice.enums.EntityStatus;
import com.elara.authorizationservice.enums.NotificationType;
import com.elara.authorizationservice.enums.ResponseCode;
import com.elara.authorizationservice.enums.GroupType;
import com.elara.authorizationservice.exception.AppException;
import com.elara.authorizationservice.exception.UnAuthorizedException;
import com.elara.authorizationservice.domain.*;
import com.elara.authorizationservice.repository.*;
import com.elara.authorizationservice.util.AppUtil;
import com.elara.authorizationservice.util.HashUtil;
import com.elara.authorizationservice.util.JWTTokens;
import com.elara.authorizationservice.util.PasswordEncoder;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class AuthenticationService {

  final UserRepository userRepository;
  final CompanyRepository companyRepository;
  final GroupRepository groupRepository;
  final UserGroupRepository userGroupRepository;
  final UserGroupPermissionRepository userGroupPermissionRepository;
  final UserLoginRepository userLoginRepository;
  final ApplicationAccountRepository applicationAccountRepository;
  final ModelMapper modelMapper;
  final MessageService messageService;
  final NotificationService notificationService;
  final PasswordEncoder passwordEncoder;
  final JWTTokens jwtTokens;
  final ApplicationService applicationService;
  final UserGroupService userGroupService;
  final NotificationCacheService notificationCacheService;

  @Value("${spring.mail.username}")
  String senderMail;

  @Value("${sms.sender}")
  String senderSms;

  public AuthenticationService(UserRepository userRepository,
                               CompanyRepository companyRepository,
                               GroupRepository groupRepository,
                               UserGroupRepository userGroupRepository,
                               UserGroupPermissionRepository userGroupPermissionRepository,
                               UserLoginRepository userLoginRepository,
                               ApplicationAccountRepository applicationAccountRepository,
                               ModelMapper modelMapper,
                               MessageService messageService,
                               NotificationService notificationService,
                               PasswordEncoder passwordEncoder,
                               JWTTokens jwtTokens,
                               ApplicationService applicationService,
                               UserGroupService userGroupService,
                               NotificationCacheService notificationCacheService) {
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.groupRepository = groupRepository;
    this.userGroupRepository = userGroupRepository;
    this.userGroupPermissionRepository = userGroupPermissionRepository;
    this.userLoginRepository = userLoginRepository;
    this.applicationAccountRepository = applicationAccountRepository;
    this.modelMapper = modelMapper;
    this.messageService = messageService;
    this.notificationService = notificationService;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokens = jwtTokens;
    this.applicationService = applicationService;
    this.userGroupService = userGroupService;
    this.notificationCacheService = notificationCacheService;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public UserRegisterResponse registerUser(UserRegisterRequest dto) {
    User existing  = userRepository.findByEmail(dto.getEmail());
    if (existing != null) {
      throw new AppException(messageService.getMessage("User.Email.Exist"));
    }

    existing = userRepository.findByPhone(dto.getPhone());
    if (existing != null) {
      throw new AppException(messageService.getMessage("User.Phone.Exist"));
    }

    Company company = companyRepository.findByClientId(RequestUtil.getClientId());
    if (company == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    User newEntry = userRepository.save(User.builder()
            .email(dto.getEmail())
            .phone(dto.getPhone())
            .companyCode(company.getCompanyCode())
            .isEmailVerified(false)
            .isPhoneVerified(false)
            .lang(Locale.getDefault().getLanguage())
            .status(EntityStatus.Enabled.name())
            .createdBy(GroupType.Customer.name())
            .createdAt(new Date())
            .build());

    userLoginRepository.save(UserLogin.builder()
            .password(passwordEncoder.encode(dto.getPassword()))
            .status(EntityStatus.Disabled.name())//Until phone and email is verified
            .createdAt(new Date())
            .userId(newEntry.getId())
            .companyCode(newEntry.getCompanyCode())
            .uuid(UUID.randomUUID().toString())
            .build());

    String groupName = GroupType.Customer.name();
    Group group = groupRepository.findByGroupNameAndCompanyCode(groupName,
        newEntry.getCompanyCode());

    if (group == null) {
      throw new AppException(messageService.getMessage("Group.NotFound").replace("{0}", groupName));
    }

    UserGroup userGroup =  new UserGroup();
    userGroup.setUserId(newEntry.getId());
    userGroup.setGroupId(group.getId());
    userGroup.setCompanyCode(company.getCompanyCode());
    userGroupRepository.save(userGroup);

    //Send otp to verify email via Notification Service
    notificationService.sendNotification(NotificationRequest.builder()
            .requiredValidation(true)
            .validationType(NotificationType.EmailVerify)
            .recipientEmail(newEntry.getEmail())
            .senderEmail(senderMail)
            .message(messageService.getMessage("message.email.verify"))
            .companyCode(newEntry.getCompanyCode())
            .subject(messageService.getMessage("email.verify.subject"))
            .recipientPhone(null)
            .build(), AppUtil.generateOtp());

    //Send otp to verify phone via Notification Service
    notificationService.sendNotification(NotificationRequest.builder()
            .requiredValidation(true)
            .validationType(NotificationType.PhoneVerify)
            .senderPhone(senderSms)
            .senderEmail(null)
            .message(messageService.getMessage("message.phone.verify"))
            .companyCode(newEntry.getCompanyCode())
            .subject(messageService.getMessage("phone.verify.subject"))
            .recipientPhone(newEntry.getPhone())
            .build(), AppUtil.generateOtp());

    UserRegisterResponse response = new UserRegisterResponse();
    response.setResponseMessage(messageService.getMessage("User.Register.Success"));
    return response;
  }

  public UserLoginResponse login(UserLoginRequest dto) {
    Company company = companyRepository.findByClientId(RequestUtil.getClientId());
    if (company == null) {
      throw new AppException(messageService.getMessage("Company.Not.Found"));
    }

    if (EntityStatus.Disabled.name().equalsIgnoreCase(company.getStatus())) {
      throw new AppException(messageService.getMessage("Company.Account.Disabled"));
    }

    User user = userRepository.findByCompanyCodeAndEmailOrPhone(company.getCompanyCode(), dto.getUsername());
    if (user == null) {
      log.info("User not found for company:{}, username:{}", company.getCompanyCode(), dto.getUsername());
      throw new AppException(messageService.getMessage("Login.Failed"));
    }

    UserLogin userLogin = userLoginRepository.findByUserId(user.getId());
    if (userLogin == null) {
      log.info("UserLogin not found for userId:{}", user.getId());
      throw new AppException(messageService.getMessage("Login.Failed"));
    }

    if (!passwordEncoder.matches(dto.getPassword(), userLogin.getPassword())) {
      log.info("UserLogin password does not match for email: {}", user.getEmail());
      throw new AppException(messageService.getMessage("Login.Failed"));
    }

    List<String> audience = applicationService.getAudience(user.getId());

    AuthToken authToken = modelMapper.map(user, AuthToken.class);
    String accessToken = jwtTokens.generateAccessToken(company, dto.getUsername());
    String refreshToken = jwtTokens.generateRefreshToken(company, dto.getUsername());
    authToken.setAccessToken(accessToken);
    authToken.setAudience(audience);
    authToken.setCompanyName(company.getCompanyName());
    authToken.setUsername(dto.getUsername());
    authToken.setUuid(userLogin.getUuid());
    authToken.setRefreshToken(refreshToken);
    authToken.setEmailVerified(user.isEmailVerified());
    authToken.setPhoneVerified(user.isPhoneVerified());
    authToken.setExpires(jwtTokens.parseJWT(accessToken).getExpiration().toString());

    userLogin.setAccessToken(accessToken);
    userLogin.setRefreshToken(refreshToken);
    userLoginRepository.save(userLogin);

    return UserLoginResponse.builder()
            .data(authToken)
            .build();
  }

  public UserLogoutResponse logout() {
    String token = RequestUtil.getToken();
    Claims claims = jwtTokens.parseJWT(token);
    String companyCode = (String) claims.get("issuer");
    Company company = companyRepository.findByCompanyCode(companyCode);
    String username = (String) claims.get("subject");

    User user = userRepository.findByCompanyCodeAndEmailOrPhone(company.getCompanyCode(), username);
    if (user == null) {
      log.info("User not found for company:{}, username:{}", company.getCompanyCode(), username);
      throw new AppException(messageService.getMessage("User.Not.Found"));
    }

    UserLogin userLogin = userLoginRepository.findByUserIdAndAccessToken(user.getId(), token);
    if (userLogin == null) {
      log.info("UserLogin not found for userId:{}", user.getId());
      throw new AppException(messageService.getMessage("Token.Not.Found"));
    }

    userLogin.setAccessToken("");
    userLogin.setRefreshToken("");

    userLoginRepository.save(userLogin);
    return new UserLogoutResponse();
  }

  public AccessTokenResponse getAccessTokenFromRefreshToken(AccessTokenRequest dto) {
    Claims claims = jwtTokens.parseRefreshJWT(dto.getRefreshToken());

    log.info("Claims: {}", new Gson().toJson(claims));

    String companyCode = (String) claims.get("issuer");
    Company company = companyRepository.findByCompanyCode(companyCode);
    String username =  (String) claims.get("subject");

    User user = userRepository.findByCompanyCodeAndEmailOrPhone(company.getCompanyCode(), username);
    if (user == null) {
      log.info("User not found for company:{}, username:{}", company.getCompanyCode(), username);
      throw new AppException(messageService.getMessage("User.Not.Found"));
    }

    UserLogin userLogin = userLoginRepository.findByUserId(user.getId());
    if (userLogin == null) {
      log.info("UserLogin not found for userId:{}", user.getId());
      throw new AppException(messageService.getMessage("User.Not.Found"));
    }

    if (!dto.getRefreshToken().equals(userLogin.getRefreshToken())) {
      log.info("Refresh token not match for userId:{}", username);
      throw new UnAuthorizedException(messageService.getMessage("Token.Fraud"));
    }

    List<String> audience = applicationService.getAudience(user.getId());

    AuthToken authToken = modelMapper.map(user, AuthToken.class);
    String accessToken = jwtTokens.generateAccessToken(company, username);
    String refreshToken = jwtTokens.generateRefreshToken(company, username);
    authToken.setAccessToken(accessToken);
    authToken.setAudience(audience);
    authToken.setUsername(username);
    authToken.setRefreshToken(refreshToken);
    authToken.setCompanyName(company.getCompanyName());
    authToken.setUuid(userLogin.getUuid());
    authToken.setLang(user.getLang());
    authToken.setExpires(jwtTokens.parseJWT(accessToken).getExpiration().toString());

    userLogin.setAccessToken(accessToken);
    userLogin.setRefreshToken(refreshToken);
    userLoginRepository.save(userLogin);

    return AccessTokenResponse.builder()
            .data(authToken)
            .build();

  }

  private boolean isAuthenticated(String token, User user) {
    Long userId  = user.getId();
    UserLogin userLogin = userLoginRepository.findByUserIdAndAccessToken(userId, token);
    if (userLogin == null) {
      throw new AppException(messageService.getMessage("Token.Not.Found"));
    }

    if (!token.equals(userLogin.getAccessToken())) {
      log.info("Token not match for userId:{}", user.getId());
      throw new UnAuthorizedException(messageService.getMessage("Token.Fraud"));
    }

    return true;
  }

  /**
   *
   * @param endpoint is a hash value of SHA 256 of appName,permission e.g authorization-service,VERIFY_EMAIL_OTP
   * @return true if the user has the permission to call the endpoint, otherwise return false
   */
  public boolean isAuthorized(String endpoint, User user) {
    /* Super admin profile required for setup */
    if (user.getEmail().equalsIgnoreCase("system@system.com")) {
      return true;
    }

    Long userId  = user.getId();

    //Check for path variable as the permissionId hashed might fail
    ApplicationPermission resource = applicationService.getByPermissionId(endpoint);

    if (resource == null) {
      throw new AppException(messageService.getMessage("App.Permission.NotFound"));
    }

    if (!resource.isSecured()) {
      return true;
    }

    List<ApplicationAccount> userPermissions = applicationAccountRepository.findByUserId(userId);
    for (ApplicationAccount userPermission : userPermissions) {
      if (resource.getId().equals(userPermission.getId())) {
        return true;
      }
    }

    List<Long> groupIds = userGroupService.groupIds(userId);
    List<UserGroupPermission> groupPermissions = userGroupPermissionRepository.findByGroupIdIn(groupIds);
    List<Long> applicationPermissionIds = new ArrayList<>();
    for (UserGroupPermission groupPermission : groupPermissions) {
      applicationPermissionIds.add(groupPermission.getApplicationPermissionId());
    }

    return applicationPermissionIds.contains(resource.getId());
  }

  public TokenVerifyResponse verifyToken(TokenVerifyRequest request) {
    TokenVerifyResponse response = new TokenVerifyResponse();
    response.setResponseCode(ResponseCode.UN_AUTHORIZED.getValue());
    response.setResponseMessage(messageService.getMessage("Auth.UnAuthorized"));

    //Client id of the service on application table
    String serviceClientId = request.getServiceClientId();

    Application application = applicationService.getByPublicKey(serviceClientId);
    if (application == null) {
      throw new AppException(messageService.getMessage("App.Setup.NotFound"));
    }

    if (EntityStatus.Shutdown.name().equals(application.getStatus())) {
      throw new AppException(messageService.getMessage("Application.Shutdown"));
    }

    if (EntityStatus.Disabled.name().equals(application.getStatus())) {
      throw new AppException(messageService.getMessage("Application.Disabled"));
    }

    //Token forwarded by API Gateway or frontend client
    String userToken = request.getToken();

    //SHA 256 hash of service-name, METHOD, path uri forwarded by the called service
    String endpoint = request.getPermissionId();

    Claims claims = jwtTokens.parseJWT(userToken);
    String username = (String) claims.get("subject");
    String companyCode = (String) claims.get("issuer");

    Company company = companyRepository.findByCompanyCode(companyCode);
    if (company == null) {
      throw new AppException(ResponseCode.UN_AUTHORIZED.getValue(), messageService.getMessage("Company.Not.Found"));
    }

    if (EntityStatus.Disabled.name().equals(company.getStatus())) {
      throw new AppException(messageService.getMessage("Company.Account.Disabled"));
    }

    User user = userRepository.findByCompanyCodeAndEmailOrPhone(companyCode, username);
    if (user == null) {
      throw new AppException(messageService.getMessage("User.Not.Found"));
    }

    if (EntityStatus.Disabled.name().equals(user.getStatus())) {
      throw new UnAuthorizedException(ResponseCode.UN_AUTHORIZED.getValue(), messageService.getMessage("User.Account.Disabled"));
    }

    UserLogin userLogin = userLoginRepository.findByCompanyCodeAndUserId(companyCode, user.getId());
    if (userLogin == null) {
      throw new AppException(messageService.getMessage("Company.NotFound"));
    }

    if (isAuthenticated(userToken, user)) {
      if (isAuthorized(endpoint, user)) {
        response.setResponseCode(ResponseCode.SUCCESSFUL.getValue());
        response.setResponseMessage(messageService.getMessage("Auth.Successful"));
      } else {
        response.setResponseCode(ResponseCode.FORBIDDEN.getValue());
        response.setResponseMessage(messageService.getMessage("Auth.Forbidden"));
      }
    } else {
      response.setResponseCode(ResponseCode.UN_AUTHORIZED.getValue());
      response.setResponseMessage(messageService.getMessage("Auth.UnAuthorized"));
    }

    response.setData(TokenVerifyResponse.Data.builder()
                    .loginId(userLogin.getUuid())
                    .companyCode(userLogin.getCompanyCode())
                    .username(username)
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .status(userLogin.getStatus())
                    .build());

    return response;
  }

  public OtpVerifyResponse verifyOtp(String otp, NotificationType notificationType) {
    String companyCode = RequestUtil.getAuthToken().getCompanyCode();
    String username = RequestUtil.getAuthToken().getUsername();
    User user = userRepository.findByCompanyCodeAndEmailOrPhone(companyCode, username);
    if (user == null) {
      throw new AppException(messageService.getMessage("User.Not.Found"));
    }

    boolean isValidOtp = notificationCacheService.isValid(companyCode, user.getId(), notificationType, otp);
    OtpVerifyResponse response = new OtpVerifyResponse();
    if (isValidOtp) {
      response.setResponseCode(ResponseCode.SUCCESSFUL.getValue());
      response.setResponseMessage(messageService.getMessage("Otp.Verify.Success"));

      if (NotificationType.EmailVerify.equals(notificationType)) {
        user.setEmailVerified(true);
      }

      if (NotificationType.PhoneVerify.equals(notificationType)) {
        user.setPhoneVerified(true);
      }

      userRepository.save(user);

      notificationCacheService.deleteUsedOtp(companyCode, user.getId(), notificationType, otp);

      return response;
    }

    throw new AppException(messageService.getMessage("Otp.Verify.Fail"));
  }

  public OtpResendResponse resendPhoneOtp() {
    notificationService.sendNotification(NotificationRequest.builder()
            .requiredValidation(true)
            .validationType(NotificationType.PhoneVerify)
            .senderPhone(senderSms)
            .senderEmail(null)
            .message(messageService.getMessage("message.phone.verify"))
            .companyCode(RequestUtil.getAuthToken().getCompanyCode())
            .subject(messageService.getMessage("phone.verify.subject"))
            .recipientPhone(RequestUtil.getAuthToken().getPhone())
            .build(), AppUtil.generateOtp());
    OtpResendResponse resp = new OtpResendResponse();
    resp.setResponseCode(ResponseCode.SUCCESSFUL.getValue());
    resp.setResponseMessage(messageService.getMessage("Message.Successful"));
    return resp;
  }

  public OtpResendResponse resendEmailOtp() {
    notificationService.sendNotification(NotificationRequest.builder()
                    .requiredValidation(true)
                    .validationType(NotificationType.EmailVerify)
                    .senderEmail(senderMail)
                    .senderPhone(null)
                    .message(messageService.getMessage("message.email.verify"))
                    .companyCode(RequestUtil.getAuthToken().getCompanyCode())
                    .subject(messageService.getMessage("email.verify.subject"))
                    .recipientEmail(RequestUtil.getAuthToken().getEmail())
                    .build(), AppUtil.generateOtp());
    OtpResendResponse resp = new OtpResendResponse();
    resp.setResponseCode(ResponseCode.SUCCESSFUL.getValue());
    resp.setResponseMessage(messageService.getMessage("Message.Successful"));
    return resp;
  }

  public OtpVerifyResponse verifyEmailOtp(String otp) {
    return verifyOtp(otp, NotificationType.EmailVerify);
  }

  public OtpVerifyResponse verifyPhoneOtp(String otp) {
    return verifyOtp(otp, NotificationType.PhoneVerify);
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public ResetPasswordResponse resetPassword(ResetPasswordRequest dto) {
    UserLogin userLogin = userLoginRepository.findByUuid(RequestUtil.getAuthToken().getUuid());

    boolean isValid = notificationCacheService.isValid(userLogin.getCompanyCode(), userLogin.getUserId(), NotificationType.ResetPasswordVerify, dto.getOtp());

    if (!isValid) {
      throw new AppException(messageService.getMessage("Otp.Verify.Fail"));
    }

    if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
      throw new AppException(messageService.getMessage("ConfirmPassword.Mismatch"));
    }

    userLogin.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    userLoginRepository.save(userLogin);
    return new ResetPasswordResponse();
  }

  public ResetPasswordInitiateResponse resetPasswordInitiate(String username) {
    User user = userRepository.findByUsername(username);

    if (user == null) {
      throw new AppException(messageService.getMessage("User.Not.Found"));
    }

    String otp = AppUtil.generateOtp();

    notificationService.sendNotification(NotificationRequest.builder()
            .requiredValidation(true)
            .validationType(NotificationType.ResetPasswordVerify)
            .senderEmail(senderMail)
            .message(messageService.getMessage("message.email.reset-password"))
            .companyCode(user.getCompanyCode())
            .subject(messageService.getMessage("email.reset-password.subject"))
            .recipientEmail(user.getEmail())
            .build(), otp);

    notificationService.sendNotification(NotificationRequest.builder()
            .requiredValidation(true)
            .validationType(NotificationType.ResetPasswordVerify)
            .senderPhone(senderSms)
            .message(messageService.getMessage("message.phone.reset-password"))
            .companyCode(user.getCompanyCode())
            .subject(messageService.getMessage("phone.reset-password.subject"))
            .recipientPhone(user.getPhone())
            .build(), otp);

    ResetPasswordInitiateResponse response = ResetPasswordInitiateResponse.builder()
            .build();
    response.setResponseCode(ResponseCode.SUCCESSFUL.getValue());
    response.setResponseMessage(messageService.getMessage("Message.Successful"));
    response.setData(ResetPasswordInitiateResponse.Data.builder()
                    .otpHash(HashUtil.getHash(otp))
                    .build());
    return response;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public ChangePasswordResponse changePassword(ChangePasswordRequest dto) {
    String loginId = RequestUtil.getAuthToken().getUuid();
    UserLogin userLogin = userLoginRepository.findByUuid(loginId);
    if (userLogin == null) {
      throw new AppException(messageService.getMessage("invalid.login"));
    }

    if (!passwordEncoder.matches(dto.getCurrentPassword(), userLogin.getPassword())) {
      throw new AppException(messageService.getMessage("Password.Mismatch"));
    }

    if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
      throw new AppException(messageService.getMessage("ConfirmPassword.Mismatch"));
    }

    userLogin.setPassword(passwordEncoder.encode(dto.getNewPassword()));

    userLoginRepository.save(userLogin);

    return new ChangePasswordResponse();
  }
}
