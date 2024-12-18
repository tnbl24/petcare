package org.datn.petcare.controller.admin;

import jakarta.persistence.criteria.Predicate;
import org.datn.petcare.dto.PageResponse;
import org.datn.petcare.dto.RequestDTO;
import org.datn.petcare.dto.ServiceDTO;
import org.datn.petcare.dto.UserDTO;
import org.datn.petcare.entity.Blog;
import org.datn.petcare.entity.User;
import org.datn.petcare.mapper.UserMapper;
import org.datn.petcare.repository.UserRepository;
import org.datn.petcare.service.admin.Impl.FilterSpecification;
import org.datn.petcare.service.admin.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/user-account-ctrl")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilterSpecification<User> filterSpecification;

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUserAccounts() {
        List<UserDTO> userDTOS= userAccountService.getAll();
        return ResponseEntity.ok(userDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserAccountById(@PathVariable String id) {
        UserDTO userDTO = userAccountService.getById(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserDTO>> getActiveUserAccounts() {
        List<UserDTO> userDTOS = userAccountService.getAllActive();
        return ResponseEntity.ok(userDTOS);
    }

    @GetMapping("/active/page/{page}")
    public ResponseEntity<PageResponse<UserDTO>> getByPage(
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserDTO> result = userAccountService.getActiveByPage(page, size);
        PageResponse<UserDTO> response = new PageResponse<>(result.getContent(), result.getTotalPages());
        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<UserDTO> createUserAccount(@RequestBody UserDTO userDTO) {
        UserDTO createdUserDTO = userAccountService.create(userDTO);
        return ResponseEntity.ok(createdUserDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserAccount(@PathVariable String id, @RequestBody UserDTO userDTO) {
        UserDTO createdUserDTO = userAccountService.update(id, userDTO);
        return ResponseEntity.ok(createdUserDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteUserAccount(@PathVariable String id) {
        userAccountService.delete(id);
        return "Deleted user account";
    }

    @PostMapping("/active/filter/page/{page}")
    public ResponseEntity<PageResponse<UserDTO>> getByFilterPage(
            @RequestBody RequestDTO requestDTO,
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {

        Specification<User> specification = filterSpecification.getSearchSpecification(
                requestDTO.getSearchRequestDTO(),
                requestDTO.getGlobalOperator()
        );

        Specification<User> activeSpecification = (root, query, criteriaBuilder) -> {
            Predicate isActivePredicate = criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("isRegistered"), true),
                    criteriaBuilder.equal(root.get("isDeleted"), false)
            );
            return criteriaBuilder.and(isActivePredicate, specification.toPredicate(root, query, criteriaBuilder));
        };

        // Tạo PageRequest với sắp xếp tăng dần theo fullName
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("fullName").ascending());

        // Lấy kết quả từ service
        Page<User> result = userAccountService.getActiveByFilterPage(activeSpecification, pageRequest);

        // Chuyển đổi từ Page<User> sang Page<UserDTO>
        Page<UserDTO> userDTOPage = result.map(userMapper::toDTO);

        // Tạo PageResponse
        PageResponse<UserDTO> response = new PageResponse<>(
                userDTOPage.getContent(),
                userDTOPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }
}
