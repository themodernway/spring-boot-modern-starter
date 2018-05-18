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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import com.themodernway.server.core.NanoTimer
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.mongodb.support.MongoDBTrait
import com.themodernway.server.sql.support.GSQLGroovyTrait

import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
@RestController
@RequestMapping('/rest')
class GroovyRestServices implements CoreGroovyTrait, MongoDBTrait, GSQLGroovyTrait
{
    @Autowired
    private PasswordEncoder m_encoder

    @GetMapping('/')
    def root()
    {
        json(paths: ['/build', '/name', '/mongo', '/jquery'])
    }

    @GetMapping('/build')
    def build()
    {
        getBuildDescriptors()
    }

    @GetMapping('/name')
    def name()
    {
        json(name: 'dean', age: 54, uuid: uuid())
    }

    @GetMapping('/mongo')
    def mongo()
    {
        json(collection('users').findOne(QUERY(name: 'dean')))
    }

    @GetMapping('/jquery')
    def jquery()
    {
        jquery('jquery', 'SELECT * FROM testing')
    }

    @GetMapping('/remote')
    def remote()
    {
        def opers = network()
        def timer = nstimer()
        def posts = (1..500).collect { int id ->
            opers.get('https://jsonplaceholder.typicode.com/comments/{id}', parameters(id: id)).json()
        }
        json(time: timer.toString(), remote: posts)
    }

    @GetMapping('/posts')
    def posts()
    {
        def opers = network()
        def timer = nstimer()
        def posts = parallel(1..500).collect { int id ->
            opers.get('https://jsonplaceholder.typicode.com/comments/{id}', parameters(id: id)).json()
        }
        json(time: timer.toString(), posts: posts)
    }

    @GetMapping('/pass/{text}')
    def pass(@PathVariable String text)
    {
        json(pass: text, encoded: m_encoder.encode(text))
    }

    @PostMapping('/echo')
    def echo(@RequestBody JSONObject body)
    {
        json(body: body)
    }

    @Memoized
    def getBuildDescriptors()
    {
        getBuildDescriptorProvider().toJSONObject()
    }

    def NanoTimer nstimer()
    {
        new NanoTimer()
    }
}
