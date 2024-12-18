package org.datn.petcare.service.user;

import org.datn.petcare.dto.PetDTO;
import org.datn.petcare.entity.User;

import java.util.List;

public interface PetService {
    List<PetDTO> getAllByUserId(String userId);
    PetDTO getById(int id);
    PetDTO create(PetDTO pet);
    PetDTO update(int id, PetDTO pet);
    void delete(int id);
}
