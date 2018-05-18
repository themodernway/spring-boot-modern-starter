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

import org.slf4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;

public class ApplicationPasswordEncoder extends BCryptPasswordEncoder implements PasswordEncoder, IHasLogging
{
    private final Logger m_logs = LoggingOps.getLogger(getClass());

    @Override
    public String encode(final CharSequence password)
    {
        if (logger().isInfoEnabled())
        {
            logger().info("encode()");
        }
        return super.encode(password);
    }

    @Override
    public boolean matches(final CharSequence password, final String encoded)
    {
        if (logger().isInfoEnabled())
        {
            logger().info("matches()");
        }
        return super.matches(password, encoded);
    }

    @Override
    public Logger logger()
    {
        return m_logs;
    }
}
