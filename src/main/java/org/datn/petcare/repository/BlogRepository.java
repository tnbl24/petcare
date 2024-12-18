package org.datn.petcare.repository;

import org.datn.petcare.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("Blog")
public interface BlogRepository extends JpaRepository<Blog, Integer> , JpaSpecificationExecutor<Blog> {

}
