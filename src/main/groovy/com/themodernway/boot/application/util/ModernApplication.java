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

package com.themodernway.boot.application.util;

import java.util.TimeZone;

public final class ModernApplication
{
    public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");

    private ModernApplication()
    {
    }

    public static void app(final Class<?> type, final String... args)
    {
        setDefaultTimeZone();
    }

    public static TimeZone getDefaultTimeZone()
    {
        return TimeZone.getDefault();
    }

    public static TimeZone setDefaultTimeZone()
    {
        return setDefaultTimeZone(DEFAULT_TIMEZONE);
    }

    public static TimeZone setDefaultTimeZone(final TimeZone zone)
    {
        TimeZone.setDefault(zone);

        return getDefaultTimeZone();
    }
}
