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

package com.themodernway.boot.application.rest.services

import javax.servlet.http.HttpSession

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import com.themodernway.common.api.java.util.CommonOps
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.core.support.spring.boot.BootServiceSupport

import groovy.transform.CompileStatic

@CompileStatic
@RestController
@RequestMapping('/monitor')
class GroovyMonitor extends BootServiceSupport implements CoreGroovyTrait
{
    @GetMapping()
    def root()
    {
        json(mappings: getMappingsList())
    }

    @GetMapping('/user')
    def user()
    {
        json(user: getUserDetails())
    }

    @PostMapping('/echo')
    def echo(@RequestBody JSONObject body)
    {
        json(body: body)
    }

    @GetMapping('/admin')
    @PreAuthorize("hasAuthority('ADMIN')")
    def admin()
    {
        json(good: true)
    }

    @GetMapping('/session')
    def session(HttpSession session)
    {
        if (session.isNew())
        {
            setAttribute(session, 'uuid', uuid())

            setAttribute(session, 'last', CommonOps.getCurrentClock())
        }
        def valu = toJSONObject(session)

        setAttribute(session, 'last', CommonOps.getCurrentClock())

        valu
    }
}
