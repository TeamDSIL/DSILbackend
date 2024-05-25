package com.ssg.dsilbackend.config;

import com.ssg.dsilbackend.jwt.*;
import com.ssg.dsilbackend.jwt.JWTFilter;
import com.ssg.dsilbackend.jwt.LoginFilter;
import com.ssg.dsilbackend.oAuth2.CustomOAuth2UserService;
import com.ssg.dsilbackend.oAuth2.CustomSuccessHandler;
import com.ssg.dsilbackend.repository.PermissionManageRepository;
import com.ssg.dsilbackend.repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //AuthenticationManager 가 인자로 받을 AuthenticationConfiguration 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    //JWTUtil 주입
    private final JWTUtil jwtUtil;
    private final PermissionManageRepository permissionManageRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final RefreshRepository refreshRepository;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        // cors 설정
        http
                .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
                        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);
                        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

                        return configuration;
                    }
                })));

        //csrf disable
        http
                .csrf(AbstractHttpConfigurer::disable);
        //From 로그인 방식 disable
        http
                .formLogin(AbstractHttpConfigurer::disable);
        http
                //http basic 인증 방식 disable
                .httpBasic(AbstractHttpConfigurer::disable);

        //JWTFilter 추가 @
        http
                .addFilterBefore(new JWTFilter(jwtUtil, permissionManageRepository), UsernamePasswordAuthenticationFilter.class);

        //경로별 인가 작업

        http
                .authorizeHttpRequests((auth) -> auth
//                        .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.*", "/*/icon-*").permitAll()
//                        .requestMatchers("/","/signup*","/login*").permitAll()
//                        .requestMatchers("/user").hasAuthority("USER")
//                        .requestMatchers("/manager").hasAuthority("MANAGER")
//                        .requestMatchers("/admin").hasAuthority("ADMIN")

//                        .requestMatchers("/memberManage/loginPage", "/", "/memberManage/signupPage", "/oauth2/**","/memberManage/adminManageUserPage").permitAll()
                        .anyRequest().permitAll());
               
//
        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/memberManage/loginPage")
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                        .failureUrl("/memberManage/loginPage?error=true"));

        //JWTFilter 추가
        http
                .addFilterAfter(new JWTFilter(jwtUtil, permissionManageRepository), OAuth2LoginAuthenticationFilter.class);
        http
                .addFilterAt(new JWTFilter(jwtUtil, permissionManageRepository), LoginFilter.class);
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
        http
                .addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}