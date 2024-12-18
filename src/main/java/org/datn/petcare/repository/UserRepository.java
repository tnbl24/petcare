package org.datn.petcare.repository;

import org.datn.petcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> , JpaSpecificationExecutor<User> {
    User findByUsername(String username);
    User findByPhone(String phone);
    User findByEmail(String email);
    User findByResetToken(String token);
}
