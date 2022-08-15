package com.sparta.sp5miniserver.configuration;


import com.sparta.sp5miniserver.utils.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {

  private final TokenProvider tokenProvider;
//  private final AuthenticationEntryPointException authenticationEntryPointException;
//  private final AccessDeniedHandlerException accessDeniedHandlerException;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  public WebSecurityCustomizer webSecurityCustomizer() {
    // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
    return (web) -> web.ignoring()
            .antMatchers("/h2-console/**");
  }

  @Bean
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors();
    http.headers().frameOptions().disable();
    http.csrf().disable()

//        .exceptionHandling()  //---->예외처리 기능이 작동
//        .authenticationEntryPoint(authenticationEntryPointException)//->인증실패시 처리
//        .accessDeniedHandler(accessDeniedHandlerException) //->인가실패시 처리

        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()
        .authorizeRequests()
        .antMatchers("/api/signup").permitAll()
        .antMatchers("/api/login").permitAll()
            .antMatchers("/h2-console/**").permitAll()
//        .antMatchers("/api/post").permitAll()
//        .antMatchers("/api/post/*").permitAll()
//        .antMatchers("/api/comment/*").permitAll()
        .anyRequest().authenticated()

        .and()
        .apply(new JwtSecurityConfiguration(tokenProvider));

    return http.build();
  }
}
