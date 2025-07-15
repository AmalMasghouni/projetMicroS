package com.programming.techie.userProfileService.repository;

import com.programming.techie.userProfileService.model.UserProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, Long> {


    public  UserProfile findByExternalId(String id);
}
