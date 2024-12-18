package org.datn.petcare.service.admin;

import org.datn.petcare.dto.ServiceDTO;
import org.datn.petcare.dto.UserDTO;
import org.datn.petcare.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UserAccountService {
    UserDTO loginByUserName(String username);
    UserDTO getUserByUsername(String username);
    List<UserDTO> getAll();
    List<UserDTO> getAllActive();

    Page<UserDTO> getActiveByPage(int page, int size);

    UserDTO getById(String id);

    UserDTO create(UserDTO user);
    UserDTO update(String id,UserDTO user);
    void delete(String id);
    Page<User> getActiveByFilterPage(Specification<User> specification, Pageable pageable);
}
