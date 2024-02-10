package com.Jason.DevInternHubBackend.web;

import org.springframework.boot.test.context.SpringBootTest;
import org.atteo.evo.inflector.English;

import com.Jason.DevInternHubBackend.domain.Company;

@SpringBootTest
public class CompanyControllerTest extends EntityControllerTest {

	@Override
	void setSampleEntityPostBodies() throws Exception {
		for (int i = 0; i < 3; i++) {
			sampleEntityPostBodies.add(String.format("{\"companyName\":\"company%d\"}", i));
		}
	}

	@Override
	void setEntityNameLowerCasePlural() {
		entityNameLowerCasePlural = English.plural(Company.class.getSimpleName()).toLowerCase();

	}

	@Override
	void setSampleEntityPatchBodies() throws Exception {
		for (int i = 0; i < 3; i++) {
			sampleEntityPatchBodies.add(String.format("{\"url\":\"url%d\"}", i));
		}

	}

}