package com.michael.librarymanager.repository;

import com.michael.librarymanager.model.Roles;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer> {
  Optional<Roles> findByName(String name);
}
