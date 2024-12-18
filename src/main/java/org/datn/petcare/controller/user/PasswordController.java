package org.datn.petcare.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import org.datn.petcare.entity.User;
import org.datn.petcare.repository.UserRepository;
import org.datn.petcare.service.admin.Impl.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/password")
public class PasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/forgot")
    public String showForgotPasswordForm() {
        return "user/forgot-password";
    }

    @PostMapping("/forgot")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Email không tồn tại trong hệ thống! Vui lòng nhập lại");
            return "redirect:/password/forgot";
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Đặt lại mật khẩu");
        message.setText("Nhấp vào liên kết để đặt lại mật khẩu: " +
                "http://localhost:8080/password/reset?token=" + token);

        try {
            mailSender.send(message);
            redirectAttributes.addFlashAttribute("success", "Email đã được gửi đến địa chỉ của bạn.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi gửi email: " + e.getMessage());
        }

        return "redirect:/login";
    }

    @GetMapping("/reset")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        User user = userRepository.findByResetToken(token);
        if (user == null) {
            model.addAttribute("error", "Token không hợp lệ.");
            return "user/reset-password";
        }
        model.addAttribute("token", token);
        return "user/reset-password";
    }

    @PostMapping("/reset")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword,
                                @RequestParam String confirmPassword, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByResetToken(token);
        if (user == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Token không hợp lệ hoặc đã hết hạn.");
            return "redirect:/password/reset?token=" + token;
        }

        if (newPassword.length() < 4) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu phải có ít nhất 4 ký tự.");
            return "redirect:/password/reset?token=" + token;
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu không khớp.");
            return "redirect:/password/reset?token=" + token;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Mật khẩu đã được đặt lại thành công! Vui lòng đăng nhập.");
        return "redirect:/login";
    }
}