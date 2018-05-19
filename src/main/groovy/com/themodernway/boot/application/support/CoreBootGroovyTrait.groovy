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

package com.themodernway.boot.application.support

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

import com.themodernway.server.core.NanoTimer
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.support.CoreGroovyTrait

import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
public trait CoreBootGroovyTrait implements CoreGroovyTrait {
    @Memoized
    public JSONObject getBuildDescriptors() {
        getBuildDescriptorProvider().toJSONObject()
    }

    public NanoTimer nstimer() {
        new NanoTimer()
    }

    public Authentication getAuthentication() {
        SecurityContextHolder.getContext().getAuthentication()
    }

    public UserDetails getUserDetails() {
        def user
        def auth = getAuthentication()
        if (auth) {
            def prin = auth.getPrincipal()
            if (prin instanceof UserDetails) {
                user = prin
            }
        }
        user as UserDetails
    }

    public <T extends RequestAttributes> T getCurrentRequestAttributes(Class<T> type = RequestAttributes) {
        def attr = RequestContextHolder.getRequestAttributes()
        ((attr) && (type.isInstance(attr))) ? type.cast(attr) : null
    }
}
