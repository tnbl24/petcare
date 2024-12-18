package org.datn.petcare.mapper;

import java.util.Arrays;
import javax.annotation.processing.Generated;
import org.datn.petcare.dto.BlogDTO;
import org.datn.petcare.entity.Blog;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-09T21:25:40+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class BlogMapperImpl implements BlogMapper {

    @Override
    public Blog toEntity(BlogDTO blogDTO) {
        if ( blogDTO == null ) {
            return null;
        }

        Blog.BlogBuilder blog = Blog.builder();

        blog.id( blogDTO.getId() );
        blog.title( blogDTO.getTitle() );
        byte[] image = blogDTO.getImage();
        if ( image != null ) {
            blog.image( Arrays.copyOf( image, image.length ) );
        }
        blog.description( blogDTO.getDescription() );
        blog.blogDetail( blogDTO.getBlogDetail() );

        return blog.build();
    }

    @Override
    public BlogDTO toDto(Blog blog) {
        if ( blog == null ) {
            return null;
        }

        BlogDTO.BlogDTOBuilder blogDTO = BlogDTO.builder();

        blogDTO.id( blog.getId() );
        blogDTO.title( blog.getTitle() );
        byte[] image = blog.getImage();
        if ( image != null ) {
            blogDTO.image( Arrays.copyOf( image, image.length ) );
        }
        blogDTO.description( blog.getDescription() );
        blogDTO.blogDetail( blog.getBlogDetail() );

        return blogDTO.build();
    }
}
