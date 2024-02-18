package com.Jason.DevInternHubBackend.web;

import org.springframework.boot.test.context.SpringBootTest;
import org.atteo.evo.inflector.English;

import com.Jason.DevInternHubBackend.domain.Technology;

@SpringBootTest
public class TechnologyControllerTest extends EntityControllerTest {

	@Override
	void setEntityNameLowerCasePlural() {
		entityNameLowerCasePlural = English.plural(Technology.class.getSimpleName()).toLowerCase();

	}

}
