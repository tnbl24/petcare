package org.datn.petcare.repository;

import org.datn.petcare.entity.Role;
import org.springdoc.core.providers.JavadocProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


public interface RoleRepository extends JpaRepository<Role, String> {
}
