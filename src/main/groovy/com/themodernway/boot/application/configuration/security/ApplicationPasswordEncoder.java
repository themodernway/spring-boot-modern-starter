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

import org.springframework.security.crypto.password.PasswordEncoder;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.security.ICryptoProvider;
import com.themodernway.server.core.security.tools.Hashing;
import com.themodernway.server.core.security.tools.ICheckSum;

public class ApplicationPasswordEncoder implements PasswordEncoder
{
    private final ICheckSum       m_crc32;

    private final ICryptoProvider m_crypt;

    public ApplicationPasswordEncoder(final ICryptoProvider crypt)
    {
        m_crc32 = Hashing.crc32();

        m_crypt = CommonOps.requireNonNull(crypt, "CryptoProvider was null.");
    }

    @Override
    public String encode(final CharSequence password)
    {
        final String stringpw = password.toString();

        return m_crypt.encrypt(m_crypt.makeBCrypt(m_crypt.sha512(stringpw, m_crc32.tohex(stringpw), 50000)));
    }

    @Override
    public boolean matches(final CharSequence password, final String encoded)
    {
        final String stringpw = password.toString();

        return m_crypt.testBCrypt(m_crypt.sha512(stringpw, m_crc32.tohex(stringpw), 50000), m_crypt.decrypt(encoded));
    }
}
