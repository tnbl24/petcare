package org.datn.petcare.service.admin.Impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.datn.petcare.dto.PetDTO;
import org.datn.petcare.dto.UserDTO;
import org.datn.petcare.entity.Pet;
import org.datn.petcare.entity.Role;
import org.datn.petcare.entity.Services;
import org.datn.petcare.entity.User;
import org.datn.petcare.mapper.PetMapper;
import org.datn.petcare.mapper.UserMapper;
import org.datn.petcare.repository.PetRepository;
import org.datn.petcare.repository.UserRepository;
import org.datn.petcare.service.admin.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserAccountImpl implements UserAccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetMapper petMapper;

    @Override
    public UserDTO loginByUserName(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(user.getUsername());
            userDTO.setPassword(user.getPassword());
            userDTO.setImage(user.getImage());
            userDTO.setFullName(user.getFullName());

            if (user.getRole() != null) {
                userDTO.setRole(user.getRole().getRole());
            }
            return userDTO;
        }
        return null;
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(user.getUsername());
            userDTO.setFullName(user.getFullName());
            userDTO.setImage(user.getImage());

            if (user.getRole() != null) {
                userDTO.setRole(user.getRole().getRole());
            }
            return userDTO;
        }
        return null;
    }

    @Override
    public List<UserDTO> getAll() {
        log.info("getAll");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> {
                    UserDTO userDTO = userMapper.toDTO(user);
                    List<Pet> pets = petRepository.findByUser(user);
                    List<PetDTO> petDTOs = pets.stream()
                            .map(petMapper::toDTO)
                            .collect(Collectors.toList());
                    userDTO.setPets(petDTOs);
                    return userDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getAllActive() {
        List<User> users = userRepository.findAll();
        List<User> filteredUsers = users.stream()
                .filter(user -> user.isRegistered() && !user.isDeleted())
                .toList();

        return filteredUsers.stream()
                .map(user -> {
                    UserDTO userDTO = userMapper.toDTO(user);
                    List<Pet> pets = petRepository.findByUser(user);
                    List<PetDTO> petDTOs = pets.stream()
                            .map(petMapper::toDTO)
                            .collect(Collectors.toList());
                    userDTO.setPets(petDTOs);
                    return userDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDTO> getActiveByPage(int page, int size) {
        List<User> allUsers = userRepository.findAll();

        List<User> filteredUsers = allUsers.stream()
                .filter(user -> user.isRegistered() && !user.isDeleted())
                .sorted(Comparator.comparing(User::getFullName))
                .collect(Collectors.toList());

        int start = (int) PageRequest.of(page, size).getOffset();
        int end = Math.min(start + size, filteredUsers.size());

        List<User> paginatedUsers = filteredUsers.subList(start, end);

        List<UserDTO> userDTOs = paginatedUsers.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(userDTOs, PageRequest.of(page, size), filteredUsers.size());
    }

    @Override
    public UserDTO getById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with uid: " + id));

        UserDTO userDTO = userMapper.toDTO(user);

        List<Pet> pets = petRepository.findByUser(user);
        List<PetDTO> petDTOs = pets.stream()
                .map(petMapper::toDTO)
                .collect(Collectors.toList());
        userDTO.setPets(petDTOs);
        return userDTO;
    }

    @Override
    public UserDTO create(UserDTO userDTO) {
        User existingPhone = userRepository.findByPhone(userDTO.getPhone());
        User existingEmail = userRepository.findByEmail(userDTO.getEmail());

        if (existingPhone != null) {
            if (!existingPhone.isRegistered()) {
                if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()) {
                    User existingUsername = userRepository.findByUsername(userDTO.getUsername());
                    if (existingUsername != null) {
                        throw new RuntimeException("Username already registered.");
                    } else {
                        existingPhone.setUsername(userDTO.getUsername());
                    }
                }

                if (existingEmail != null) {
                    throw new RuntimeException("Email already registered.");
                }

                if (userDTO.getPassword() != null) {
                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
                    existingPhone.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                }

                existingPhone.setFullName(userDTO.getFullName());
                existingPhone.setImage(userDTO.getImage());
                existingPhone.setGender(userDTO.isGender());
                existingPhone.setEmail(userDTO.getEmail());
                existingPhone.setAddress(userDTO.getAddress());
                existingPhone.setDeleted(false);
                existingPhone.setRegistered(true);

                existingPhone = userRepository.save(existingPhone);
                return userMapper.toDTO(existingPhone);
            } else {
                throw new RuntimeException("This phone already registered.");
            }
        } else {
            if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()) {
                User existingUsername = userRepository.findByUsername(userDTO.getUsername());
                if (existingUsername != null) {
                    throw new RuntimeException("Username already registered.");
                }
            }

            if (existingEmail != null) {
                throw new RuntimeException("Email already registered.");
            }

            User newUser = userMapper.toEntity(userDTO);

            if (userDTO.getPassword() != null) {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
                newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            } else {
                newUser.setPassword("");
            }

            newUser.setDeleted(false);
            newUser.setRegistered(true);

            Role defaultRole = new Role();
            defaultRole.setRole("user");
            newUser.setRole(defaultRole);

            newUser = userRepository.save(newUser);
            return userMapper.toDTO(newUser);
        }
    }

    @Override
    public UserDTO update(String id, UserDTO user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Kiểm tra tên người dùng
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            User existingUsername = userRepository.findByUsername(user.getUsername());
            if (existingUsername != null && !existingUsername.getId().equals(existingUser.getId())) {
                throw new RuntimeException("Username already registered.");
            } else {
                existingUser.setUsername(user.getUsername());
            }
        }

        // Cập nhật mật khẩu
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Cập nhật thông tin khác
        existingUser.setFullName(user.getFullName());
        existingUser.setImage(user.getImage());
        existingUser.setGender(user.isGender());
        existingUser.setEmail(user.getEmail());

        // Kiểm tra số điện thoại
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            User existingPhone = userRepository.findByPhone(user.getPhone());
            if (existingPhone != null && existingPhone.isRegistered() && !existingPhone.getId().equals(existingUser.getId())) {
                throw new RuntimeException("Phone number already exists.");
            } else {
                existingUser.setPhone(user.getPhone());
            }
        }

        // Kiểm tra email
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            User existingEmail = userRepository.findByEmail(user.getEmail());
            if (existingEmail != null && !existingEmail.getId().equals(existingUser.getId())) {
                throw new RuntimeException("Email already registered.");
            } else {
                existingUser.setEmail(user.getEmail());
            }
        }

        existingUser.setAddress(user.getAddress());

        existingUser = userRepository.save(existingUser);
        return userMapper.toDTO(existingUser);
    }

    @Override
    public void delete(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service with ID " + id + " not found"));

        user.setDeleted(true);
        user.setRegistered(false);
        userRepository.save(user);
    }

    @Override
    public Page<User> getActiveByFilterPage(Specification<User> specification, Pageable pageable) {
        return userRepository.findAll(specification, pageable);
    }
}
