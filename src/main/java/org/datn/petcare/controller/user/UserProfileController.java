package org.datn.petcare.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.datn.petcare.dto.UserDTO;
import org.datn.petcare.entity.User;
import org.datn.petcare.mapper.UserMapper;
import org.datn.petcare.repository.UserRepository;
import org.datn.petcare.service.admin.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

@Controller
@RequestMapping("/user-profile")
@Slf4j
public class UserProfileController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @GetMapping
    public String userProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        UserDTO userDTO = userMapper.toDTO(user);

        if (userDTO.getImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(userDTO.getImage());
            userDTO.setBase64Img(base64Image);
        }

        log.info("UserDTO: {}", userDTO);
        model.addAttribute("user", userDTO);
        model.addAttribute("editable", false);
        return "user/user-profile";
    }

    @PostMapping("/update")
    public String updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            UserDTO userDTO,
            Model model) {

        User user = userRepository.findByUsername(userDetails.getUsername());

        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());

        if (userDTO.getImageFile() != null && !userDTO.getImageFile().isEmpty()) {
            try {
                user.setImage(userDTO.getImageFile().getBytes());
            } catch (IOException e) {
                log.error("Error updating user image: {}", e.getMessage());
            }
        }

        userRepository.save(user);

        model.addAttribute("user", userMapper.toDTO(user));
        model.addAttribute("editable", false);
        return "redirect:/user-profile";
    }

    @GetMapping("/edit")
    public String editUser(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        UserDTO userDTO = userMapper.toDTO(user);
        log.info("Editing UserDTO: {}", userDTO);
        model.addAttribute("user", userDTO);
        model.addAttribute("editable", true);
        return "user/user-profile";
    }
}