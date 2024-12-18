package org.datn.petcare.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class URLController {
    @GetMapping("login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/about")
    public String about() {
        return "user/about";
    }


    @GetMapping("contact")
    public String contact() {
        return "user/contact";
    }

    @GetMapping("errorpage")
    public String error() {
        return "user/error";
    }

    @GetMapping("/booking-success")
    public String bookingSuccess() {
        return "user/booking-success";
    }

    @GetMapping("/cancell-payment")
    public String cancellPayment() {
        return "user/cancell-payment";
    }
}
