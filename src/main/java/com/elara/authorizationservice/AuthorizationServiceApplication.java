package com.elara.authorizationservice;

import com.elara.authorizationservice.dto.request.SyncPermissionRequest;
import com.elara.authorizationservice.service.ApplicationService;
import com.elara.authorizationservice.service.CompanyService;
import com.elara.authorizationservice.service.PermissionService;
import com.elara.authorizationservice.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@EnableJpaRepositories({"com.elara.authorizationservice.repository"})
@ComponentScan({"com.elara.authorizationservice"})
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
public class AuthorizationServiceApplication extends SpringBootServletInitializer implements CommandLineRunner {

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	ApplicationService applicationService;

	@Autowired
	CompanyService companyService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Value("${app.public-key}")
	public String publicKey;

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServiceApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AuthorizationServiceApplication.class);
	}

	@Override
	public void run(String... args) throws Exception {

		SyncPermissionRequest syncPermissionRequest = permissionService.generatePermissionRequest(requestMappingHandlerMapping);
		permissionService.syncApplicationPermission(syncPermissionRequest);
		//System.out.println("SyncPermissionRequest: " + new Gson().toJson(syncPermissionRequest));
		/*Map<String, String> keys = RSAUtil.generateKeyPair();
		System.out.println("public-key: " + keys.get("public-key"));
		System.out.println("private-key: " + keys.get("private-key"));*/

	}
}
