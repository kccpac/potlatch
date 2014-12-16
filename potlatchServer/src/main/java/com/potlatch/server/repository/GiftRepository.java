package com.potlatch.server.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftRepository extends CrudRepository<Gift, Long> {

	public Gift findByTitle(
			@Param("title") String title);
	
	public Gift findById(
			@Param("id") long id);
	
	public Collection<Gift> findByOwnerId(
			@Param("ownerId") long ownerId
			);
}

