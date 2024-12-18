package org.datn.petcare.controller.admin;

import org.datn.petcare.dto.BlogDTO;
import org.datn.petcare.dto.BookedServiceDTO;
import org.datn.petcare.dto.PageResponse;
import org.datn.petcare.dto.RequestDTO;
import org.datn.petcare.entity.Blog;
import org.datn.petcare.mapper.BlogMapper;
import org.datn.petcare.repository.BlogRepository;
import org.datn.petcare.service.admin.BlogService;
import org.datn.petcare.service.admin.Impl.FilterSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/blog-ctrl")
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private FilterSpecification<Blog> filterSpecification;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private BlogMapper blogMapper;

    @GetMapping("/all")
    public ResponseEntity<List<BlogDTO>> getALl(){
        List<BlogDTO> bookedServices = blogService.getAll();
        return ResponseEntity.ok(bookedServices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogDTO> findById(@PathVariable int id) {
        return blogRepository.findById(id)
                .map(blog -> ResponseEntity.ok(blogMapper.toDto(blog))) // Chuyển đổi thành BlogDTO và trả về
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)); // Trả về 404 nếu không tìm thấy
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<PageResponse<BlogDTO>> getBlogs(
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BlogDTO> result = blogService.getByPage(page, size);
        PageResponse<BlogDTO> response = new PageResponse<>(result.getContent(), result.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    String deleteBlog(@PathVariable int id) {
        blogService.delete(id);
        return "Blog deleted";
    }

    @PostMapping()
    public ResponseEntity<BlogDTO> saveBlog(@ModelAttribute BlogDTO blogDTO) {
        BlogDTO blogDTO1 = blogService.create(blogDTO);
        return  new ResponseEntity<>(blogDTO1, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogDTO> updateBlog(@PathVariable int id, @ModelAttribute BlogDTO blogDTO) {
        BlogDTO blogDTO1 = blogService.update(id, blogDTO);
        return  new ResponseEntity<>(blogDTO1, HttpStatus.OK);
    }

    @PostMapping("/filter")
    public List<BlogDTO> filter(@RequestBody RequestDTO requestDTO) {
        Specification<Blog> specification = filterSpecification.getSearchSpecification(requestDTO.getSearchRequestDTO(),requestDTO.getGlobalOperator());
        List<Blog> blogs = blogRepository.findAll(specification);

        return blogs.stream()
                .map(blogMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/filter/page/{page}")
    public ResponseEntity<PageResponse<BlogDTO>> filterWithPagination(
            @RequestBody RequestDTO requestDTO,
            @PathVariable int page,
            @RequestParam(defaultValue = "10") int size) {

        Specification<Blog> specification = filterSpecification.getSearchSpecification(
                requestDTO.getSearchRequestDTO(),
                requestDTO.getGlobalOperator()
        );

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Blog> result = blogRepository.findAll(specification, pageRequest);

        PageResponse<BlogDTO> response = new PageResponse<>(
                result.getContent().stream()
                        .map(blogMapper::toDto)
                        .collect(Collectors.toList()),
                result.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }
}
