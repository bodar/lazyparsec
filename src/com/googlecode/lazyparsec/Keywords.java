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

import com.googlecode.lazyparsec.annotations.Private;
import com.googlecode.lazyparsec.functors.Maps;
import com.googlecode.totallylazy.Callable1;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Helper class for creating lexers and parsers for keywords.
 *
 * @author Ben Yu
 */
final class Keywords {

    private interface StringCase {
        Comparator<String> comparator();

        String toKey(String k);

        <T> Callable1<String, T> toMap(java.util.Map<String, T> m);
    }

    private static final StringCase CASE_SENSITIVE = new CaseSensitive();
    private static final StringCase CASE_INSENSITIVE = new CaseInsensitive();

    private static class CaseSensitive implements StringCase {
        private static Comparator<String> COMPARATOR = new Comparator<String>() {
            public int compare(String a, String b) {
                if (a == b) return 0;
                else if (a == null) return -1;
                else if (b == null) return 1;
                else return a.compareTo(b);
            }
        };

        public Comparator<String> comparator() {
            return COMPARATOR;
        }

        public String toKey(String k) {
            return k;
        }

        public <T> Callable1<String, T> toMap(java.util.Map<String, T> m) {
            return Maps.map(m);
        }
    }

    private static class CaseInsensitive implements StringCase {
        private static Comparator<String> COMPARATOR = new Comparator<String>() {
            public int compare(String a, String b) {
                if (a == b) return 0;
                else if (a == null) return -1;
                else if (b == null) return 1;
                else return a.compareToIgnoreCase(b);
            }
        };

        public Comparator<String> comparator() {
            return COMPARATOR;
        }

        public String toKey(String k) {
            return k.toLowerCase();
        }

        public <T> Callable1<String, T> toMap(
                final java.util.Map<String, T> m) {
            return new Callable1<String, T>() {
                public T call(String key) {
                    return m.get(key.toLowerCase());
                }
            };
        }
    }

    private static StringCase getStringCase(boolean caseSensitive) {
        return caseSensitive ? CASE_SENSITIVE : CASE_INSENSITIVE;
    }

    @Private
    static String[] unique(Comparator<String> c, String... names) {
        TreeSet<String> set = new TreeSet<String>(c);
        set.addAll(Arrays.asList(names));
        return set.toArray(new String[set.size()]);
    }

    @SuppressWarnings("unchecked")
    static <T> Lexicon lexicon(Parser<String> wordScanner, String[] keywordNames, boolean caseSensitive, final Callable1<String, ?> defaultCallable1) {
        StringCase scase = getStringCase(caseSensitive);
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (String n : unique(scase.comparator(), keywordNames)) {
            Object value = Tokens.reserved(n);
            map.put(scase.toKey(n), value);
        }
        final Callable1<String, Object> fmap = scase.toMap(map);
        Callable1<String, Object> tokenizerCallable1 = new Callable1<String, Object>() {
            public Object call(String text) throws Exception {
                Object val = fmap.call(text);
                if (val != null) return val;
                else return defaultCallable1.call(text);
            }
        };
        return new Lexicon(fmap, wordScanner.map(tokenizerCallable1));
    }
}
