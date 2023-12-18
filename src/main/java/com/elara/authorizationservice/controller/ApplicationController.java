package com.elara.authorizationservice.controller;

import com.elara.authorizationservice.auth.Permission;
import com.elara.authorizationservice.dto.request.CreateApplicationRequest;
import com.elara.authorizationservice.dto.request.UpdateApplicationRequest;
import com.elara.authorizationservice.dto.response.CreateApplicationResponse;
import com.elara.authorizationservice.dto.response.UpdateApplicationResponse;
import com.elara.authorizationservice.service.ApplicationService;
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
@Tag(name = "Application Account Management", description = "Application Account Management")
public class ApplicationController {

  final ApplicationService applicationService;

  public ApplicationController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @Operation(summary = "Create New Application or Service")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Create New Application or Service",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = CreateApplicationResponse.class))})})
  @Permission("CREATE_APP")
  @PostMapping("/application/create")
  public ResponseEntity<CreateApplicationResponse> createApplication(@Valid @RequestBody CreateApplicationRequest dto){
    return ResponseEntity.ok(applicationService.createApplication(dto));
  }

  @Operation(summary = "Update Application or Service")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update Application or Service",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UpdateApplicationResponse.class))})})
  @Permission("UPDATE_APP")
  @PostMapping("/application/update")
  public ResponseEntity<UpdateApplicationResponse> updateApplication(@Valid @RequestBody UpdateApplicationRequest dto){
    return ResponseEntity.ok(applicationService.updateApplication(dto));
  }
}
