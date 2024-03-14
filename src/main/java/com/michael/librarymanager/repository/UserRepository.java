package com.michael.librarymanager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.michael.librarymanager.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
  
  Boolean existsByUsername(String username);
  Optional<UserEntity> findByUsername(String username);
}
