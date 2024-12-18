package org.datn.petcare.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String index() {
        return "admin/index";
    }

    @GetMapping("booked-service")
    public String bookedService() {
        return "admin/booked-service";
    }

    @GetMapping("group-service")
    public String groupService() {
        return "admin/group-service";
    }

    @GetMapping("service")
    public String services() {
        return "admin/service";
    }

    @GetMapping("service-detail")
    public String serviceDetail() {
        return "admin/service-detail";
    }

    @GetMapping("/user-account")
    public String userAccount() {
        return "admin/user-account";
    }

    @GetMapping("/blog")
    public String blog() {
        return "admin/blog";
    }

    @GetMapping("/blog-edit")
    public String editBlog() {
        return "admin/blog-edit";
    }

    @GetMapping("/blog-create")
    public String blogCreate() {
        return "admin/blog-create";
    }
}
