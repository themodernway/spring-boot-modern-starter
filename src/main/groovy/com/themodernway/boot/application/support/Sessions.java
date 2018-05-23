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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.json.JSONObject;

public final class Sessions
{
    private Sessions()
    {
    }

    public static final List<JSONObject> getMappingsList(final RequestMappingHandlerMapping mapping, final String prefix)
    {
        return CommonOps.toList(mapping.getHandlerMethods().keySet().stream().map(Sessions::getRequestMappingInfo).filter(json -> json.getAsString("path").startsWith(prefix)));
    }

    public static final JSONObject getRequestMappingInfo(final RequestMappingInfo info)
    {
        final Set<RequestMethod> meth = info.getMethodsCondition().getMethods();

        return new JSONObject("method", meth.isEmpty() ? RequestMethod.GET : CommonOps.toList(meth).get(0)).set("path", CommonOps.toList(info.getPatternsCondition().getPatterns()).get(0));

    }

    public static final JSONObject toJSONObject(final HttpSession sess)
    {
        final JSONObject attr = new JSONObject();

        getAttributeNames(sess).stream().filter(name -> false == name.contains("SECURITY")).forEach(name -> attr.set(name, sess.getAttribute(name)));

        return new JSONObject().set("id", sess.getId()).set("new", sess.isNew()).set("created", sess.getCreationTime()).set("accessed", sess.getLastAccessedTime()).set("interval", sess.getMaxInactiveInterval()).set("attributes", attr);
    }

    public static final List<String> getAttributeNames(final HttpSession sess)
    {
        return CommonOps.toUnmodifiableList(CommonOps.requireNonNullOrElse(sess.getAttributeNames(), () -> Collections.emptyEnumeration()));
    }

    public static final <T> T getAttribute(final HttpSession sess, final String name, final Class<T> type)
    {
        return SAFE(sess.getAttribute(name), type);
    }

    public static final <T, V> T getValueOuElse(final V valu, final Supplier<T> good, final Supplier<T> nope)
    {
        return getValueOuElse(valu, CommonOps::isNonNull, good, nope);
    }

    public static final <T, V> T getValueOuElse(final V valu, final Predicate<V> look, final Supplier<T> good, final Supplier<T> nope)
    {
        return look.test(valu) ? good.get() : nope.get();
    }

    public static final <T, R> R getValueOuElse(final T valu, final Function<T, R> good, final Function<T, R> nope)
    {
        return getValueOuElse(valu, CommonOps::isNonNull, good, nope);
    }

    public static final <T, R> R getValueOuElse(final T valu, final Predicate<T> look, final Function<T, R> good, final Function<T, R> nope)
    {
        return look.test(valu) ? good.apply(valu) : nope.apply(valu);
    }

    public static final <T> T setAttribute(final HttpSession sess, final String name, final T attr)
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

    public static final <T> T setAttribute(final HttpSession sess, final String name, final Supplier<T> otherwise)
    {
        return setAttribute(sess, name, otherwise.get());
    }

    public static final <T> T getAttributeOrElse(final HttpSession sess, final String name, final Class<T> type, final T otherwise)
    {
        return CommonOps.requireNonNullOrElse(getAttribute(sess, name, type), otherwise);
    }

    public static final <T> T getAttributeOrElse(final HttpSession sess, final String name, final Class<T> type, final Supplier<T> otherwise)
    {
        return CommonOps.requireNonNullOrElse(getAttribute(sess, name, type), otherwise);
    }

    public static final <T> T getAttributeOrElseSet(final HttpSession sess, final String name, final Class<T> type, final T otherwise)
    {
        final T attr = getAttribute(sess, name, type);

        return (null == attr) ? setAttribute(sess, name, otherwise) : CommonOps.NULL();
    }

    public static final <T> T getAttributeOrElseSet(final HttpSession sess, final String name, final Class<T> type, final Supplier<T> otherwise)
    {
        final T attr = getAttribute(sess, name, type);

        return (null == attr) ? setAttribute(sess, name, otherwise.get()) : CommonOps.NULL();
    }

    public static final <T> T SAFE(final Object valu, final Class<T> type)
    {
        if ((null != valu) && (type.isInstance(valu)))
        {
            return type.cast(valu);
        }
        return null;
    }
}
