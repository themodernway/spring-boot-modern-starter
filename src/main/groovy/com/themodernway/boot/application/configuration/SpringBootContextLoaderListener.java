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

package com.themodernway.boot.application.configuration;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;
import com.themodernway.server.core.support.spring.ServerContextInstance;

public class SpringBootContextLoaderListener extends ContextLoaderListener implements IHasLogging
{
    private final Logger m_logs = LoggingOps.getLogger(getClass());

    @Override
    public Logger logger()
    {
        return m_logs;
    }

    @Override
    public void contextInitialized(final ServletContextEvent event)
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "SpringBootContextLoaderListener.contextInitialized() STARTING");
        }
        ServerContextInstance.setApplicationContext(WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()));

        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "SpringBootContextLoaderListener.contextInitialized() COMPLETE");
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event)
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "SpringBootContextLoaderListener.contextDestroyed() STARTING");
        }
        ServerContextInstance.setApplicationContext(null);

        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "SpringBootContextLoaderListener.contextDestroyed() COMPLETE");
        }
        LoggingOps.stop();
    }
}
