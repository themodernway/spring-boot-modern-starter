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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.themodernway.server.core.security.CorePasswordEncoder;
import com.themodernway.server.core.security.ICryptoProvider;

@EnableWebSecurity
@EnableRedisHttpSession
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration
{
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception
    {
        auth.inMemoryAuthentication().withUser("root").password("fd665330ebb8ee0c00934123651b7d386563a1bca3b9fdfe92a5c548968391b0076ebd9abb25735f9d6aac92f301a57bbde0129c4026bb4c3d47c6b9f575d36c5a825c1badf44ab9741a2ad11e370b4a31e30a36741e241435e19898").authorities("ADMIN", "ACTUATOR").and().withUser("user").password("7110f603eb824e5a16c43ee2b274f25f66472de65d8708d9937837b663e66880d0041a119fb546a60f7eae5d12a7e45332b435579f0cb6fc26c7eaf030e6662477766ca2889dac602bf1932178e6fd43ce4f54890ae5c0422acb52f2").authorities("USER");
    }

    @Configuration
    public static class ActuatorSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter
    {
        @Override
        protected void configure(final HttpSecurity http) throws Exception
        {
            http.antMatcher("/service/**").csrf().disable();

            http.antMatcher("/content/**").headers().disable().csrf().disable();

            http.requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests().anyRequest().hasAuthority("ACTUATOR").and().httpBasic();
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder(@Autowired final ICryptoProvider crypt)
    {
        return new CorePasswordEncoder(crypt);
    }
}