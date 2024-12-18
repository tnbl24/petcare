package org.datn.petcare.controller.user;

import org.datn.petcare.dto.UserDTO;
import org.datn.petcare.service.admin.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "user/register";
    }

    @PostMapping
    public String createUserAccount(@ModelAttribute UserDTO userDTO, RedirectAttributes redirectAttributes, Model model) {
        try {
            UserDTO createdUserDTO = userAccountService.create(userDTO);
            redirectAttributes.addFlashAttribute("message", "Tài khoản đã đăng ký thành công. Bạn có thể đăng nhập ngay bây giờ.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("userDTO", userDTO);
            return "user/register";
        }
    }
}

