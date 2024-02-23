package com.Jason.DevInternHubBackend.web;

import org.springframework.boot.test.context.SpringBootTest;
import org.atteo.evo.inflector.English;

import com.Jason.DevInternHubBackend.domain.Company;

@SpringBootTest
public class CompanyControllerTest extends EntityControllerTest {

	@Override
	void setEntityNameLowerCasePlural() {
		entityNameLowerCasePlural = English.plural(Company.class.getSimpleName()).toLowerCase();
	}
	
	// not applicable yet

}
