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

package com.themodernway.boot.application.support;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;

public class ServiceSupport implements IHasLogging
{
    @Autowired
    private PasswordEncoder              m_encoder;

    @Autowired
    private RequestMappingHandlerMapping m_handler;

    private final String                 m_mapping = ServiceOps.getPathOf(getClass());

    private final Logger                 m_logging = LoggingOps.getLogger(getClass());

    protected PasswordEncoder getPasswordEncoder()
    {
        return m_encoder;
    }

    protected RequestMappingHandlerMapping getRequestMappingHandlerMapping()
    {
        return m_handler;
    }

    protected String getServiceMappingPrefix()
    {
        return m_mapping;
    }

    protected List<JSONObject> getMappingsList(final String prefix)
    {
        return ServiceOps.getMappingsList(getRequestMappingHandlerMapping(), CommonOps.requireNonNullOrElse(getServiceMappingPrefix(), prefix), "method", "path");
    }

    protected List<JSONObject> getMappingsList()
    {
        return getMappingsList(CommonOps.NULL());
    }

    protected JSONObject toJSONObject(final HttpSession sess)
    {
        return toJSONObject(sess, "SPRING", "SECURITY");
    }

    protected JSONObject toJSONObject(final HttpSession sess, final String... list)
    {
        return toJSONObject(sess, ServiceOps.isStringInListContains(list).negate());
    }

    protected JSONObject toJSONObject(final HttpSession sess, final List<String> list)
    {
        return toJSONObject(sess, ServiceOps.isStringInListContains(list).negate());
    }

    protected JSONObject toJSONObject(final HttpSession sess, final Predicate<String> filter)
    {
        final JSONObject attr = new JSONObject();

        getSessionAttributeNames(sess).stream().filter(filter).forEach(name -> attr.set(name, sess.getAttribute(name)));

        return new JSONObject().set("id", sess.getId()).set("new", sess.isNew()).set("created", sess.getCreationTime()).set("accessed", sess.getLastAccessedTime()).set("interval", sess.getMaxInactiveInterval()).set("attributes", attr);
    }

    protected List<String> getSessionAttributeNames(final HttpSession sess)
    {
        return CommonOps.toUnmodifiableList(CommonOps.requireNonNullOrElse(sess.getAttributeNames(), () -> Collections.emptyEnumeration()));
    }

    protected <T> T getAttribute(final HttpSession sess, final String name)
    {
        return CommonOps.CAST(sess.getAttribute(name));
    }

    protected <T> T getAttribute(final HttpSession sess, final String name, final Class<T> type)
    {
        return ServiceOps.SAFE(sess.getAttribute(name), type);
    }

    protected <T> T getAttributeOrElse(final HttpSession sess, final String name, final Class<T> type, final T otherwise)
    {
        return CommonOps.requireNonNullOrElse(getAttribute(sess, name, type), otherwise);
    }

    protected <T> T getAttributeOrElse(final HttpSession sess, final String name, final Class<T> type, final Supplier<T> otherwise)
    {
        return CommonOps.requireNonNullOrElse(getAttribute(sess, name, type), otherwise);
    }

    protected <T> T getAttributeOrElseSet(final HttpSession sess, final String name, final Class<T> type, final T otherwise)
    {
        final T attr = getAttribute(sess, name, type);

        return (null == attr) ? setAttribute(sess, name, otherwise) : CommonOps.NULL();
    }

    protected <T> T getAttributeOrElseSet(final HttpSession sess, final String name, final Class<T> type, final Supplier<T> otherwise)
    {
        final T attr = getAttribute(sess, name, type);

        return (null == attr) ? setAttribute(sess, name, otherwise.get()) : CommonOps.NULL();
    }

    protected <T> T setAttribute(final HttpSession sess, final String name, final T attr)
    {
        if (null == attr)
        {
            sess.removeAttribute(name);
        }
        else
        {
            sess.setAttribute(name, attr);
        }
        return attr;
    }

    protected <T> T setAttribute(final HttpSession sess, final String name, final Supplier<T> otherwise)
    {
        return setAttribute(sess, name, otherwise.get());
    }

    protected Long getCurrentTime()
    {
        return System.currentTimeMillis();
    }

    @Override
    public Logger logger()
    {
        return m_logging;
    }

    public static final class ServiceOps
    {
        private ServiceOps()
        {
        }

        public static final <T> T SAFE(final Object valu, final Class<T> type)
        {
            if ((null != valu) && (type.isInstance(valu)))
            {
                return type.cast(valu);
            }
            return CommonOps.NULL();
        }

        public static final Predicate<String> isStringInList(final String... list)
        {
            return name -> CommonOps.toSet(list).contains(name);
        }

        public static final Predicate<String> isStringInList(final List<String> list)
        {
            return name -> list.contains(name);
        }

        public static final Predicate<String> isStringInListContains(final String... list)
        {
            return name -> CommonOps.toStream(list).anyMatch(valu -> name.contains(valu));
        }

        public static final Predicate<String> isStringInListContains(final List<String> list)
        {
            return name -> list.stream().anyMatch(valu -> name.contains(valu));
        }

        public static final String getPathOf(final Class<?> type)
        {
            return getRequestMappingPath(getRequestMapping(type));
        }

        public static final RequestMapping getRequestMapping(Class<?> type)
        {
            final RequestMapping anno = type.getAnnotation(RequestMapping.class);

            if (anno != null)
            {
                return anno;
            }
            type = type.getSuperclass();

            if (null != type)
            {
                return getRequestMapping(type);
            }
            return CommonOps.NULL();
        }

        public static final String getRequestMappingPath(final RequestMapping request)
        {
            String base = CommonOps.NULL();

            if (null != request)
            {
                final String[] path = request.path();

                if (null != path)
                {
                    final int leng = path.length;

                    if (leng > 0)
                    {
                        final String temp = StringOps.toTrimOrNull(path[0]);

                        if (null != temp)
                        {
                            base = temp;
                        }
                    }
                }
                if (null == base)
                {
                    final String[] valu = request.value();

                    if (null != valu)
                    {
                        final int leng = valu.length;

                        if (leng > 0)
                        {
                            final String temp = StringOps.toTrimOrNull(valu[0]);

                            if (null != temp)
                            {
                                base = temp;
                            }
                        }
                    }
                }
            }
            return base;
        }

        public static final List<JSONObject> getMappingsList(final RequestMappingHandlerMapping mapping, final String prefix, final String meth, final String link)
        {
            return CommonOps.toList(CommonOps.toKeys(mapping.getHandlerMethods()).stream().map(info -> getRequestMappingInfo(info, meth, link)).filter(json -> json.getAsString(link).startsWith(prefix)));
        }

        public static final JSONObject getRequestMappingInfo(final RequestMappingInfo info, final String meth, final String link)
        {
            final Set<RequestMethod> cond = info.getMethodsCondition().getMethods();

            return new JSONObject(meth, cond.isEmpty() ? RequestMethod.GET : CommonOps.toList(cond).get(0)).set(link, CommonOps.toList(info.getPatternsCondition().getPatterns()).get(0));
        }
    }
}
