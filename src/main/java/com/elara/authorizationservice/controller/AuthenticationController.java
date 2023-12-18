package com.elara.authorizationservice.controller;

import com.elara.authorizationservice.auth.Permission;
import com.elara.authorizationservice.dto.request.*;
import com.elara.authorizationservice.dto.response.*;
import com.elara.authorizationservice.service.AuthenticationService;
import com.elara.authorizationservice.service.NotificationService;
import com.elara.authorizationservice.util.AppUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@Tag(name = "Authentication Management", description = "Authentication Management")
public class AuthenticationController {

  final AuthenticationService authenticationService;
  final NotificationService notificationService;

  public AuthenticationController(AuthenticationService authenticationService,
      NotificationService notificationService) {
    this.authenticationService = authenticationService;
    this.notificationService = notificationService;
  }

  @Operation(summary = "Register New User")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Register New User",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UserRegisterResponse.class))})})
  @PostMapping("/register")
  public ResponseEntity<UserRegisterResponse> registerNewUser(@Valid @RequestBody UserRegisterRequest dto) {
    return ResponseEntity.ok(authenticationService.registerUser(dto));
  }

  @Operation(summary = "Login User")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Login User",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = UserLoginResponse.class))})})
  @PostMapping("/login")
  public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody UserLoginRequest dto) {
    return ResponseEntity.ok(authenticationService.login(dto));
  }

  @Operation(summary = "Logout User")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Logout User",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = UserLogoutResponse.class))})})
  @PutMapping("/logout")
  public ResponseEntity<UserLogoutResponse> logoutUser() {
    return ResponseEntity.ok(authenticationService.logout());
  }

  @Operation(summary = "Obtain New Token Using Refresh Token")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Obtain New Token Using Refresh Token",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = TokenVerifyResponse.class))})})
  @PostMapping("/token/refresh")
  public ResponseEntity<AccessTokenResponse> getAccessToken(@RequestBody AccessTokenRequest dto) {
    return ResponseEntity.ok(authenticationService.getAccessTokenFromRefreshToken(dto));
  }

  /**
   *
   * @param request is the request sent by registered service that needs to be secured by the authorization server
   * @return the outcome of the token verification
   */
  @Operation(summary = "Verify User Is Authorized")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Verify User Is Authorized",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = TokenVerifyResponse.class))})})
  @PostMapping("/token/verify")
  public ResponseEntity<TokenVerifyResponse> verifyToken(@RequestBody TokenVerifyRequest request) {
    return ResponseEntity.ok(authenticationService.verifyToken(request));
  }

  @Operation(summary = "Verify Registered Email")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Verify Registered Email",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = OtpVerifyResponse.class))})})
  @GetMapping("/email/{otp}/verify")
  @Permission("VERIFY_REGISTERED_EMAIL")
  public ResponseEntity<OtpVerifyResponse> verifyEmailOtp(@PathVariable("otp") String otp) {
    return ResponseEntity.ok(authenticationService.verifyEmailOtp(otp));
  }

  @Operation(summary = "Verify Registered Phone Number")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = " Registered Phone Number",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = OtpVerifyResponse.class))})})
  @GetMapping("/phone/{otp}/verify")
  @Permission("VERIFY_REGISTERED_PHONE")
  public ResponseEntity<OtpVerifyResponse> verifyPhoneOtp(@PathVariable("otp") String otp) {
    return ResponseEntity.ok(authenticationService.verifyPhoneOtp(otp));
  }

  @Operation(summary = "Resend Email Verification OTP")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Resend Email Verification OTP",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = OtpVerifyResponse.class))})})
  @GetMapping("/email/otp/resend")
  @Permission("RESEND_EMAIL_OTP")
  public ResponseEntity<OtpResendResponse> resendEmailOtp() {
    return ResponseEntity.ok(authenticationService.resendEmailOtp());
  }

  @Operation(summary = "Resend Phone Verification OTP")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Resend Phone Verification OTP",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = OtpVerifyResponse.class))})})
  @GetMapping("/phone/otp/resend")
  @Permission("RESEND_PHONE_OTP")
  public ResponseEntity<OtpResendResponse> resendPhoneOtp() {
    return ResponseEntity.ok(authenticationService.resendPhoneOtp());
  }

  @Operation(summary = "Send notification")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Send notification",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = OtpVerifyResponse.class))})})
  @PostMapping("/notification")
  public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest dto) {
    notificationService.sendNotification(dto, AppUtil.generateOtp());
    return ResponseEntity.ok(NotificationResponse.builder()
        .build());
  }

  @Operation(summary = "Verify OTP")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Verify OTP",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = NotificationVerifyResponse.class))})})
  @PostMapping("/notification/otp/verify")
  public ResponseEntity<NotificationVerifyResponse> verifyOtpNotification(@Valid @RequestBody NotificationVerifyRequest dto) {
    OtpVerifyResponse otpVerifyResponse = authenticationService.verifyOtp(dto.getOtp(), dto.getNotificationType());
    NotificationVerifyResponse response = new NotificationVerifyResponse();
    response.setResponseCode(otpVerifyResponse.getResponseCode());
    response.setResponseMessage(otpVerifyResponse.getResponseMessage());
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Change Password")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Change Password",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = ChangePasswordResponse.class))})})
  @PutMapping("/change-password")
  @Permission("CHANGE_PASSWORD")
  public ResponseEntity<ChangePasswordResponse> changePassword(@Valid @RequestBody ChangePasswordRequest dto) {
    return ResponseEntity.ok(authenticationService.changePassword(dto));
  }

  @Operation(summary = "Initiate Reset Password")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Initiate Reset Password",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = ResetPasswordInitiateResponse.class))})})
  @PutMapping("/reset-password/{username}/initiate")
  @Permission("RESET_PASSWORD_INITIATE")
  public ResponseEntity<ResetPasswordInitiateResponse> initiateResetPassword(@PathVariable("username") String username) {
    return ResponseEntity.ok(authenticationService.resetPasswordInitiate(username));
  }

  @Operation(summary = "Reset Password")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Reset Password",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = ResetPasswordResponse.class))})})
  @PutMapping("/reset-password")
  @Permission("RESET_PASSWORD")
  public ResponseEntity<ResetPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest dto) {
    return ResponseEntity.ok(authenticationService.resetPassword(dto));
  }
}
