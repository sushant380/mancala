package com.sushant.mancala.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Security configuration to support Basic Authentication for api request. */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class RestSecurityConfiguration extends WebSecurityConfigurerAdapter {

  // creating dummy users for application purpose. Can be connect to db to fetch user details.
  @Override
  protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
    authManagerBuilder
        .inMemoryAuthentication()
        .withUser("user")
        .password("{noop}password")
        .authorities("USER")
        .and()
        .withUser("admin")
        .password("{noop}admin")
        .authorities("ADMIN")
        .and()
        .withUser("player1")
        .password("{noop}player1")
        .authorities("USER")
        .and()
        .withUser("player2")
        .password("{noop}player2")
        .authorities("USER")
        .and()
        .withUser("test")
        .password("{noop}test")
        .authorities("ADMIN")
        .and()
        .withUser("TEST_PLAYER_1")
        .password("{noop}test")
        .authorities("ADMIN")
        .and()
        .withUser("TEST_PLAYER_2")
        .password("{noop}test")
        .authorities("ADMIN");
  }

  // Configure api's with and without security. Swagger ui resources are excluded from
  // authorization.
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();
    http.authorizeRequests()
        .antMatchers(
            "/",
            "/csrf",
            "/v2/api-docs",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/webjars/**")
        .permitAll()
        .antMatchers("/games**")
        .access("hasAnyAuthority('ADMIN','USER')");
    http.httpBasic()
        .authenticationEntryPoint(
            new BasicAuthenticationEntryPoint() { // << implementing this interface
              @Override
              public void commence(
                  HttpServletRequest request,
                  HttpServletResponse response,
                  AuthenticationException authException)
                  throws IOException {
                response.sendError(
                    HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
              }

              @Override
              public void afterPropertiesSet() {
                setRealmName("admin");
                super.afterPropertiesSet();
              }
            });
  }
}
