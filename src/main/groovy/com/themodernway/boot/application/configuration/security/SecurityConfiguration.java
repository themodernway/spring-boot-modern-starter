/*
 * Copyright (c) 2018, The Modern Way. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.themodernway.boot.application.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception
    {
        auth.inMemoryAuthentication().withUser("admin").password("abc123").authorities("ADMIN");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception
    {
        http.authorizeRequests().antMatchers("/services/**").permitAll().antMatchers("/actuator/**").hasAuthority("ADMIN").anyRequest().permitAll().and().httpBasic();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new ApplicationPasswordEncoder();
    }
}