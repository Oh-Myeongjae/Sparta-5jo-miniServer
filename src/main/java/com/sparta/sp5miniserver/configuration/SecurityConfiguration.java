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

  @Bean
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors();

    http.csrf().disable()

//        .exceptionHandling()  //---->예외처리 기능이 작동
//        .authenticationEntryPoint(authenticationEntryPointException)//->인증실패시 처리
//        .accessDeniedHandler(accessDeniedHandlerException) //->인가실패시 처리


        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()
        .authorizeRequests()
        .antMatchers("/api/*").permitAll()
//        .antMatchers("/api/post").permitAll()
//        .antMatchers("/api/post/*").permitAll()
//        .antMatchers("/api/comment/*").permitAll()
        .anyRequest().authenticated()

        .and()
        .apply(new JwtSecurityConfiguration(tokenProvider));

    return http.build();
  }
}
