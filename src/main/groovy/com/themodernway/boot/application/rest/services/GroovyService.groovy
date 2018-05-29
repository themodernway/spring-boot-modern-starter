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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletRequestAttributes

import com.themodernway.common.api.java.util.CommonOps
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.core.support.spring.boot.BootServiceSupport
import com.themodernway.server.mongodb.support.MongoDBGroovyTrait
import com.themodernway.server.sql.support.GSQLGroovyTrait

import groovy.transform.CompileStatic

@CompileStatic
@RestController
@RequestMapping('/service')
class GroovyService extends BootServiceSupport implements CoreGroovyTrait, MongoDBGroovyTrait, GSQLGroovyTrait
{
    @GetMapping()
    def root()
    {
        json(mappings: getMappingsList())
    }

    @GetMapping('/build')
    def build()
    {
        getBuildDescriptors()
    }

    @GetMapping('/name')
    def name()
    {
        json(name: 'Maël Hörz\u00A9', age: 54, uuid: uuid())
    }

    @GetMapping('/user')
    def user()
    {
        json(user: getUserDetails())
    }

    @GetMapping('/mongo')
    def mongo()
    {
        json(collection('users').findOne(name: 'dean'))
    }

    @GetMapping('/jquery')
    def jquery()
    {
        jquery('jquery', 'SELECT * FROM testing')
    }

    @GetMapping('/variable/{id}')
    def variable(@PathVariable('id') String id)
    {
        json(id: id)
    }

    @GetMapping('/remote')
    def remote()
    {
        def opers = network()
        def timer = nstimer()
        def posts = (1..500).collect
        { int id ->
            opers.get('https://jsonplaceholder.typicode.com/comments/{id}', parameters(id: id)).json()
        }
        json(time: timer.toString(), remote: posts)
    }

    @GetMapping('/posts')
    def posts()
    {
        def opers = network()
        def timer = nstimer()
        def posts = parallel(1..500).collect
        { int id ->
            opers.get('https://jsonplaceholder.typicode.com/comments/{id}', parameters(id: id)).json()
        }
        json(time: timer.toString(), posts: posts)
    }

    @PostMapping('/password')
    def encode(@RequestBody JSONObject body)
    {
        def pass = body['password'] as String

        json(encoded: getPasswordEncoder().encode(pass))
    }

    @GetMapping('/password')
    def password()
    {
        json(password: getPasswordEncoder().encode(getCryptoProvider().getRandomPass()))
    }

    @PostMapping('/echo')
    def echo(@RequestBody JSONObject body)
    {
        json(body: body)
    }

    @GetMapping('/attrs')
    def attrs()
    {
        def path = '-none-'
        def ctxt = '-none-'
        def attr = getCurrentRequestAttributes(ServletRequestAttributes)
        if (attr)
        {
            path = attr.getRequest().getRequestURI()
            ctxt = attr.getRequest().getContextPath()
        }
        json(path: path, ctxt: ctxt)
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
