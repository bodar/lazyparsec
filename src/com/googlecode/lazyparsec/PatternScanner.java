/*****************************************************************************
 * Copyright (C) Codehaus.org                                                *
 * ------------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 * http://www.apache.org/licenses/LICENSE-2.0                                *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 *****************************************************************************/
package com.googlecode.lazyparsec;

import com.googlecode.lazyparsec.pattern.Pattern;

/**
 * Parses a {@link Pattern}.
 *
 * @author Ben Yu
 */
final class PatternScanner extends Parser<Void> {
    private final String name;
    private final Pattern pattern;

    PatternScanner(String name, Pattern pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    @Override
    boolean apply(final ParseContext context) {
        int at = context.at;
        CharSequence src = context.characters();
        int matchLength = pattern.match(src, at, src.length());
        if (matchLength < 0) {
            context.expected(name);
            return false;
        }
        context.next(matchLength);
        context.result = null;
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}