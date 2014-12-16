package com.potlatch.server.repository;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEmotionRepository extends CrudRepository<UserEmotion, Long>{
	
	public Collection <UserEmotion> findByUserId(
			@Param("userId") long userId);

	public UserEmotion findByUserIdAndGiftId(
			@Param("userId") long userId,
			@Param("giftId") long giftId
			);
}
