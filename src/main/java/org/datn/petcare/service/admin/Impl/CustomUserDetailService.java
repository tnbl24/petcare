package org.datn.petcare.service.admin.Impl;

import org.datn.petcare.dto.UserDTO;
import org.datn.petcare.entity.CustomUserDetail;
import org.datn.petcare.entity.Role;
import org.datn.petcare.service.admin.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserAccountService userAccountService;

    @Autowired
    public CustomUserDetailService(@Lazy UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = userAccountService.loginByUserName(username);

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        Collection<GrantedAuthority> grantedAuthorities = new HashSet<>();

        String role = user.getRole();
        if (role != null) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }


        return new CustomUserDetail(user, grantedAuthorities);
    }
}

