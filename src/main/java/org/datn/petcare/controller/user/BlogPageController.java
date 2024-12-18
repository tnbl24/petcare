package org.datn.petcare.controller.user;

import jakarta.servlet.http.HttpSession;
import org.datn.petcare.dto.BlogDTO;
import org.datn.petcare.dto.PageResponse;
import org.datn.petcare.entity.Blog;
import org.datn.petcare.mapper.BlogMapper;
import org.datn.petcare.repository.BlogRepository;
import org.datn.petcare.service.admin.BlogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Optional;

@Controller
public class BlogPageController {

    private static final Logger log = LoggerFactory.getLogger(BlogPageController.class);
    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private BlogRepository blogRepository;

    @GetMapping("/blog")
    public String getBlogs(Model model) {
        return getBlogs(0, 10, model);
    }

    @GetMapping("/blog/page/{page}")
    public String getBlogs(
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<BlogDTO> result = blogService.getByPage(page, size);

        for (BlogDTO blog : result.getContent()) {
            if (blog.getImage() != null) {
                String base64Image = Base64.getEncoder().encodeToString(blog.getImage());
                blog.setBase64Img(base64Image);
            }
        }

        model.addAttribute("blogs", result.getContent());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("currentPage", page);

        return "user/blog";
    }

    @GetMapping("/blog-detail")
    public String blogDetail(@RequestParam Integer id, Model model) {
        Optional<Blog> blogOpt = blogRepository.findById(id);

        if (blogOpt.isPresent()) {
            BlogDTO blogDTO = blogMapper.toDto(blogOpt.get());

            if (blogDTO.getImage() != null) {
                String base64Image = Base64.getEncoder().encodeToString(blogDTO.getImage());
                blogDTO.setBase64Img(base64Image);
            }

            model.addAttribute("blog", blogDTO);
        } else {
            log.info("Không tìm thấy blog với ID: {}", id);
            return "redirect:/blog";
        }

        return "user/blog-detail";
    }
}
