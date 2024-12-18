package org.datn.petcare.repository;

import org.datn.petcare.entity.Pet;
import org.datn.petcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Integer> {
    List<Pet> findByUser(User user);
    Pet findByNameAndUser(String name, User user);
}
