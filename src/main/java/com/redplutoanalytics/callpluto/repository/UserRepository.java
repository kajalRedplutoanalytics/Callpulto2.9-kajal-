package com.redplutoanalytics.callpluto.repository;

import java.util.Optional;
import com.redplutoanalytics.callpluto.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
	// Find user by username
	Optional<Users> findByUsername(String username);

	// Find user by email
	Optional<Users> findByEmail(String email);

	// Check if username exists
	boolean existsByUsername(String username);

	// Check if email exists
	boolean existsByEmail(String email);

	// Get the maximum user ID
	@Query("SELECT MAX(u.id) FROM Users u")
	Optional<Long> findMaxId();
}
