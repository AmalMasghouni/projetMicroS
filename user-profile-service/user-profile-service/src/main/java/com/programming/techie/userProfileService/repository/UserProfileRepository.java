package com.programming.techie.userProfileService.repository;

import com.programming.techie.userProfileService.model.UserProfile;
import org.springframework.data.repository.CrudRepository;

public interface UserProfileRepository extends CrudRepository<UserProfile, String> {
}
