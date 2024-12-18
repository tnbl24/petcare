package org.datn.petcare.service.admin.Impl;

import org.datn.petcare.dto.BlogDTO;
import org.datn.petcare.entity.Blog;
import org.datn.petcare.mapper.BlogMapper;
import org.datn.petcare.repository.BlogRepository;
import org.datn.petcare.service.admin.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogImpl implements BlogService {

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    BlogMapper blogMapper;

    @Override
    public Page<BlogDTO> getByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return blogRepository.findAll(pageable).map(blogMapper::toDto);
    }



    @Override
    public BlogDTO create(BlogDTO blog) {
        Blog blogEntity = blogMapper.toEntity(blog);

        // Xử lý hình ảnh cho bài viết mới
        processImage(blog.getImageFile(), blogEntity);

        blogEntity = blogRepository.save(blogEntity);
        return blogMapper.toDto(blogEntity);
    }

    @Override
    public BlogDTO update(int id, BlogDTO blog) {
        Blog blogEntity = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        // Cập nhật thông tin bài viết
        blogEntity.setTitle(blog.getTitle());
        blogEntity.setBlogDetail(blog.getBlogDetail());
        blogEntity.setDescription(blog.getDescription());

        // Xử lý hình ảnh cho bài viết cập nhật
        processImage(blog.getImageFile(), blogEntity);

        blogEntity = blogRepository.save(blogEntity);
        return blogMapper.toDto(blogEntity);
    }

    private void processImage(MultipartFile imageFile, Blog blogEntity) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                blogEntity.setImage(imageFile.getBytes()); // Chuyển đổi MultipartFile thành byte[]
            } catch (IOException e) {
                throw new RuntimeException("Error while converting image file", e);
            }
        }
    }

    @Override
    public void delete(int id) {
        blogRepository.deleteById(id);
    }

    @Override
    public List<BlogDTO> getAll() {
        return blogRepository.findAll()
                .stream()
                .map(blogMapper::toDto)
                .collect(Collectors.toList());
    }
}
