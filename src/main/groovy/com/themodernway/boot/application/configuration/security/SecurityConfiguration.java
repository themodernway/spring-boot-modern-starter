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
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.themodernway.server.core.security.ICryptoProvider;

@EnableWebSecurity
@EnableRedisHttpSession
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration
{
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception
    {
        auth.inMemoryAuthentication().withUser("root").password("62587f16323c5f1e61b7e57897df9e2b0f9cc62f94b876161ca9a5fb9da3818cdf0a8405ebeec6c73bac989911dbddf735bafc8672dbfc672cfa2b389e98a7c57f397b6d6cb04435202f8738e20291edd6c2f71fa01c00b425bcc3a7").authorities("ADMIN", "ACTUATOR");
    }

    @Order(1)
    @Configuration
    public static class ContentSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter
    {
        @Override
        protected void configure(final HttpSecurity http) throws Exception
        {
            http.antMatcher("/content/**").headers().disable().csrf().disable();
        }
    }

    @Order(2)
    @Configuration
    public static class ServiceSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter
    {
        @Override
        protected void configure(final HttpSecurity http) throws Exception
        {
            http.authorizeRequests().antMatchers("/service/**").permitAll().anyRequest().authenticated().and().httpBasic();
        }
    }

    @Order(3)
    @Configuration
    public static class MonitorSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter
    {
        @Override
        protected void configure(final HttpSecurity http) throws Exception
        {
            http.authorizeRequests().antMatchers("/monitor/**").permitAll().anyRequest().authenticated().and().httpBasic();
        }
    }

    @Order(4)
    @Configuration
    public static class ActuatorSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter
    {
        @Override
        protected void configure(final HttpSecurity http) throws Exception
        {
            http.authorizeRequests().requestMatchers(EndpointRequest.toAnyEndpoint()).hasAuthority("ACTUATOR").and().cors().and().httpBasic();
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder(@Autowired final ICryptoProvider crypt)
    {
        return new ApplicationPasswordEncoder(crypt);
    }
}