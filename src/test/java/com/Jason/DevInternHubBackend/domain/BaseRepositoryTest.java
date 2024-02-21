package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.CrudRepository;

public abstract class BaseRepositoryTest<T, ID> extends BaseTest {
	// return the repository that is to be tested
	protected abstract CrudRepository<T, ID> getRepository();
	// return an instance of the domain object
	protected abstract T createEntity();

	protected CrudRepository<T, ID> crudRepository;
	protected DatabaseEntity sampleEntity1, sampleEntity2, sampleEntity3;

	@BeforeEach
	public void setUpRepositoryAndSampleData() {
		crudRepository = getRepository();
		crudRepository.deleteAll();
		sampleEntity1 = ((DatabaseEntity) createEntity()).generateFields();
		sampleEntity2 = ((DatabaseEntity) createEntity()).generateFields();
		sampleEntity3 = ((DatabaseEntity) createEntity()).generateFields();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSave() {
		assertTrue(crudRepository.count() == 0);
		// test save
		crudRepository.save((T) sampleEntity1);
		assertTrue(crudRepository.count() == 1);
		// test idempotency
		crudRepository.save((T) sampleEntity1);
		assertTrue(crudRepository.count() == 1);
		// test saveAll
		crudRepository.saveAll(Arrays.asList((T) sampleEntity2, (T) sampleEntity3));
		assertTrue(crudRepository.count() == 3);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDelete() {
		crudRepository.saveAll(Arrays.asList((T) sampleEntity1, (T) sampleEntity2, (T) sampleEntity3));
		assertTrue(crudRepository.count() == 3);
		crudRepository.delete((T) sampleEntity1);
		assertTrue(crudRepository.count() == 2);
		crudRepository.deleteById((ID) sampleEntity2.getId());
		assertTrue(crudRepository.count() == 1);
		crudRepository.deleteAll();
		assertTrue(crudRepository.count() == 0);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExistsById() {
		crudRepository.save((T) sampleEntity1);
		assertTrue(crudRepository.existsById((ID) sampleEntity1.getId()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindById() {
		crudRepository.save((T) sampleEntity1);
		ID id = (ID) sampleEntity1.getId();
		DatabaseEntity retrievedEntity = (DatabaseEntity) crudRepository.findById(id).get();
		assertTrue(sampleEntity1.getId().equals(retrievedEntity.getId()));
	}
}
