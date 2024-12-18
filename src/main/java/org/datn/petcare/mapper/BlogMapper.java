package org.datn.petcare.mapper;

import org.datn.petcare.dto.BlogDTO;
import org.datn.petcare.entity.Blog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BlogMapper {
    Blog toEntity(BlogDTO blogDTO);
    BlogDTO toDto(Blog blog);
}
