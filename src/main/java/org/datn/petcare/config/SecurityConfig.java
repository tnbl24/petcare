package org.datn.petcare.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.datn.petcare.service.admin.Impl.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((auth) -> auth
//                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers(
                                "/login", "/logout",
                                "/home","/about",
                                "/booking",
                                "/spa-groom", "/veterinary",
                                "/other","/hotel",
                                "/history",
                                "/services",
                                "/services/**",
                                "/blog","/blog/page/**",
                                "/blog-detail","/blog-detail/**",
                                "/password/forgot","/password/reset",
                                "/password/reset/**","/password/forgot/**",
                                "/errorpage","/booking-success",
                                "/confirm-booking"
                                ,"https://sandbox.vnpayment.vn/paymentv2/vpcpay.html",
                                "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html/**",
                                "/payment-callback",
                                "/cancell-payment",
                                "/admin/booked-service-ctrl/all"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasAuthority("admin")
                        .anyRequest().authenticated()
                )
                .formLogin(
                        login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler())
                )
                .logout(
                        logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .invalidateHttpSession(true)
                );
        return http.build();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(true).ignoring()
                .requestMatchers("/static/**",
                        "/admin/assets/**",
                        "/user/**",
                        "/assets/**",
                        "/my/**"
                );
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication) throws IOException {
                if (authentication.getAuthorities().stream().anyMatch(grantedAuthority
                        -> grantedAuthority.getAuthority().equals("admin"))) {
                    response.sendRedirect("/admin");
                } else {
                    response.sendRedirect("/home");
                }
            }
        };
    }
}
