package com.elara.authorizationservice.controller;

import com.elara.authorizationservice.auth.Permission;
import com.elara.authorizationservice.dto.request.CreateUserRequest;
import com.elara.authorizationservice.dto.request.UpdateUserRequest;
import com.elara.authorizationservice.dto.response.CreateUserResponse;
import com.elara.authorizationservice.dto.response.UpdateUserResponse;
import com.elara.authorizationservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "User Account Management", description = "User Account Management")
public class UserController {

  final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "Create New User Account")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Create New User Account",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = CreateUserResponse.class))})})
  @Permission("CREATE_USER")
  @PostMapping("/user/create")
  public ResponseEntity<CreateUserResponse> createNewUser(@Valid @RequestBody CreateUserRequest dto){
    return ResponseEntity.ok(userService.createUser(dto));
  }

  @Operation(summary = "Update User Account")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update User Account",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UpdateUserResponse.class))})})
  @Permission("UPDATE_USER")
  @PostMapping("/user/update")
  public ResponseEntity<UpdateUserResponse> updateUser(@Valid @RequestBody UpdateUserRequest dto){
    return ResponseEntity.ok(userService.updateUser(dto));
  }
}
