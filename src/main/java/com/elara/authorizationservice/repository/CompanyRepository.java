package com.elara.authorizationservice.repository;

import com.elara.authorizationservice.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

  Company findByClientId(String clientId);

  Company findByCompanyCode(String companyCode);

  Company findByCompanyName(String companyName);
}
