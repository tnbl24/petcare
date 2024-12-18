package org.datn.petcare.service.admin;

import org.datn.petcare.dto.BlogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogService {
    Page<BlogDTO> getByPage(int page, int size);
    BlogDTO create(BlogDTO blog);
    BlogDTO update(int id, BlogDTO blog);
    void delete(int id);

    List<BlogDTO> getAll();
}
