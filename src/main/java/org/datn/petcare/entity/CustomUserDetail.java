package org.datn.petcare.entity;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datn.petcare.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class CustomUserDetail implements UserDetails {

    private UserDTO user ;
    public Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getFullName(){
        return user.getFullName();
    }

    public String getEmail(){
        return user.getEmail();
    }

    public String getPhone(){
        return user.getPhone();
    }

    public String getAddress(){
        return user.getAddress();
    }

    public String getImgBase64() {
        if (user.getImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getImage());
            log.info("Encoded image data: " + base64Image);  // Thêm log để kiểm tra giá trị
            return base64Image;
        } else {
            log.warn("Image data is null");
            return null; // Hoặc bạn có thể trả về một giá trị mặc định
        }
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}