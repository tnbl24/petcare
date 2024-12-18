package org.datn.petcare.repository;

import org.datn.petcare.entity.BookedService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BookedServiceRepository extends JpaRepository<BookedService, Integer> , JpaSpecificationExecutor<BookedService> {
    List<BookedService> findByUser_Username(String username);
}
