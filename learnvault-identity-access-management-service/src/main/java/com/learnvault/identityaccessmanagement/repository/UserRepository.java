package com.learnvault.identityaccessmanagement.repository;

import com.learnvault.identityaccessmanagement.entity.User;
import com.learnvault.identityaccessmanagement.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByStatus(Status status);
    boolean existsByEmail(String email);
}
