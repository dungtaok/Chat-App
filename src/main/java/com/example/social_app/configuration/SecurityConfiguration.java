package com.example.social_app.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request ->
                request
                .anyRequest().permitAll());
                // .requestMatchers("/login", "/").permitAll()

        
        httpSecurity.oauth2ResourceServer(oauth2 -> // khi request gửi kèm token sẽ kiểm tra, nếu đúng sẽ cho truy cập
                oauth2.jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customJwtDecoder) // chỉ định cách giải mã
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        // vì lỗi khi xác thực tài khoản (401) xảy ra khi được kiểm tra trên filterChain nên phải xử lí khác đi
                        // .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) // thường được dùng để xử lí lỗi 401(Unauthorized) do hệ thống tự động sinh ra
        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("scope"); // lấy từ claim "scope"
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }



}

