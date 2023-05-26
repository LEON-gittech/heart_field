package com.example.heart_field.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
 
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
 
        http.authorizeRequests().antMatchers("/**").permitAll()
                .and()
                .csrf().disable();//相当于<security:scrf enabled=false/>标签
    }
 
 
}