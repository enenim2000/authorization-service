package com.elara.authorizationservice.controller;

import com.elara.authorizationservice.auth.Permission;
import com.elara.authorizationservice.dto.request.CreateCompanyRequest;
import com.elara.authorizationservice.dto.request.UpdateCompanyRequest;
import com.elara.authorizationservice.dto.response.CreateCompanyResponse;
import com.elara.authorizationservice.dto.response.UpdateCompanyResponse;
import com.elara.authorizationservice.service.CompanyService;
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
@Tag(name = "Company Management", description = "Company Management")
public class CompanyController {

  final CompanyService companyService;

  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  @Operation(summary = "Create New Company")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Create New Company",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = CreateCompanyResponse.class))})})
  @Permission("CREATE_COMPANY")
  @PostMapping("/company/create")
  public ResponseEntity<CreateCompanyResponse> createNewCompany(@Valid @RequestBody CreateCompanyRequest dto){
    return ResponseEntity.ok(companyService.createCompany(dto));
  }

  @Operation(summary = "Update Company")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Update Company",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UpdateCompanyResponse.class))})})
  @Permission("UPDATE_COMPANY")
  @PostMapping("/company/update")
  public ResponseEntity<UpdateCompanyResponse> updateCompany(@Valid @RequestBody UpdateCompanyRequest dto){
    return ResponseEntity.ok(companyService.updateCompany(dto));
  }
}
