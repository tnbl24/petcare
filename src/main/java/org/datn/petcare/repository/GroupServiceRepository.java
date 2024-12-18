package org.datn.petcare.repository;

import org.datn.petcare.entity.GroupService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupServiceRepository extends JpaRepository<GroupService, Integer> , JpaSpecificationExecutor<GroupService> {
    boolean existsByName(String name);
}
