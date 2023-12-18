package com.elara.authorizationservice.controller;

import com.elara.authorizationservice.auth.Permission;
import com.elara.authorizationservice.dto.request.*;
import com.elara.authorizationservice.dto.response.*;
import com.elara.authorizationservice.service.PermissionService;
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
@Tag(name = "Permission Management", description = "Permission Management")
public class PermissionController {

  final PermissionService permissionService;

  public PermissionController(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @Operation(summary = "Add Application Permission")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Add Application Permission",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = CreatePermissionResponse.class))})})
  @PostMapping("/permission/add")
  public ResponseEntity<CreatePermissionResponse> createApplicationPermission(@Valid @RequestBody CreatePermissionRequest dto){
    return ResponseEntity.ok(permissionService.createApplicationPermission(dto));
  }

  @Operation(summary = "Create Group")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Create Group",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = CreateGroupResponse.class))})})
  @Permission("CREATE_GROUP")
  @PostMapping("/group/create")
  public ResponseEntity<CreateGroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest dto){
    return ResponseEntity.ok(permissionService.createGroup(dto));
  }

  @Operation(summary = "Update Group")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update Group",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UpdateGroupResponse.class))})})
  @Permission("UPDATE_GROUP")
  @PostMapping("/group/update")
  public ResponseEntity<UpdateGroupResponse> updateGroup(@Valid @RequestBody UpdateGroupRequest dto){
    return ResponseEntity.ok(permissionService.updateGroup(dto));
  }

  @Operation(summary = "Assign Group(s) to User")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Assign Group(s) to User",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = AssignUserGroupResponse.class))})})
  @Permission("ASSIGN_USER_GROUP")
  @PostMapping("/user/group/assign")
  public ResponseEntity<AssignUserGroupResponse> assignUserGroup(@Valid @RequestBody AssignUserGroupRequest dto){
    return ResponseEntity.ok(permissionService.assignGroupToUser(dto));
  }

  @Operation(summary = "Assign Permission(s) to User")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Assign Permission(s) to User",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = AssignUserPermissionResponse.class))})})
  @Permission("ASSIGN_USER_PERMISSION")
  @PostMapping("/user/permission/assign")
  public ResponseEntity<AssignUserPermissionResponse> assignUserPermission(@Valid @RequestBody AssignUserPermissionRequest dto){
    return ResponseEntity.ok(permissionService.assignPermissionToUser(dto));
  }

  @Operation(summary = "Assign Permission(s) to Group")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Assign Permission(s) to Group",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = AssignGroupPermissionResponse.class))})})
  @Permission("ASSIGN_GROUP_PERMISSION")
  @PostMapping("/group/permission/assign")
  public ResponseEntity<AssignGroupPermissionResponse> assignGroupPermission(@Valid @RequestBody AssignGroupPermissionRequest dto){
    return ResponseEntity.ok(permissionService.assignPermissionToGroup(dto));
  }

  @Operation(summary = "Sync Application Permission")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Sync Application Permission",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = SyncPermissionResponse.class))})})
  @PostMapping("/permission/sync")
  public ResponseEntity<SyncPermissionResponse> syncApplicationPermission(@Valid @RequestBody SyncPermissionRequest dto){
    return ResponseEntity.ok(permissionService.syncApplicationPermission(dto));
  }
}
