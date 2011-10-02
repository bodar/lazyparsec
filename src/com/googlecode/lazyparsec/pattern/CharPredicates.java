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
package com.googlecode.lazyparsec.pattern;

import com.googlecode.lazyparsec.util.Strings;
import com.googlecode.totallylazy.Predicate;

/**
 * Provides common {@link Predicate<Character>} implementations.
 *
 * @author Ben Yu
 */
public final class CharPredicates {

    private CharPredicates() {
    }

    /**
     * A {@link Predicate<Character>} that always returns false.
     */
    public static final Predicate<Character> NEVER = new Predicate<Character>() {
        public boolean matches(Character c) {
            return false;
        }

        @Override
        public String toString() {
            return "none";
        }
    };

    /**
     * A {@link Predicate<Character>} that always returns true.
     */
    public static final Predicate<Character> ALWAYS = new Predicate<Character>() {
        public boolean matches(Character c) {
            return true;
        }

        @Override
        public String toString() {
            return "any character";
        }
    };

    /**
     * A {@link Predicate<Character>} that returns true if the character is a digit or within the range
     * of {@code [a-f]} or {@code [A-F]}.
     */
    public static final Predicate<Character> IS_HEX_DIGIT = new Predicate<Character>() {
        public boolean matches(Character c) {
            return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
        }

        @Override
        public String toString() {
            return "[0-9a-fA-F]";
        }
    };

    /**
     * A {@link Predicate<Character>} that returns true if {@link Character#isUpperCase(char)} returns
     * true.
     */
    public static final Predicate<Character> IS_UPPER_CASE = new Predicate<Character>() {
        public boolean matches(Character c) {
            return Character.isUpperCase(c);
        }

        @Override
        public String toString() {
            return "uppercase";
        }
    };

    /**
     * A {@link Predicate<Character>} that returns true if {@link Character#isLowerCase(char)} returns
     * true.
     */
    public static final Predicate<Character> IS_LOWER_CASE = new Predicate<Character>() {
        public boolean matches(Character c) {
            return Character.isLowerCase(c);
        }

        @Override
        public String toString() {
            return "lowercase";
        }
    };

    /**
     * A {@link Predicate<Character>} that returns true if {@link Character#isWhitespace(char)}
     * returns true.
     */
    public static final Predicate<Character> IS_WHITESPACE = new Predicate<Character>() {
        public boolean matches(Character c) {
            return Character.isWhitespace(c);
        }

        @Override
        public String toString() {
            return "whitespace";
        }
    };

    /**
     * A {@link Predicate<Character>} that returns true if the character is an alpha character.
     */
    public static final Predicate<Character> IS_ALPHA = new Predicate<Character>() {
        public boolean matches(Character c) {
            return c <= 'z' && c >= 'a' || c <= 'Z' && c >= 'A';
        }

        @Override
        public String toString() {
            return "[a-zA-Z]";
        }
    };

    /**
     * A {@link Predicate<Character>} that returns true if it is an alpha character or the underscore
     * character {@code _}.
     */
    public static final Predicate<Character> IS_ALPHA_ = new Predicate<Character>() {
        public boolean matches(Character c) {
            return c == '_' || c <= 'z' && c >= 'a' || c <= 'Z' && c >= 'A';
        }

        @Override
        public String toString() {
            return "[a-zA-Z_]";
        }
    };

    /**
     * A {@link Predicate<Character>} that returns true if {@link Character#isLetter(char)} returns
     * true.
     */
    public static final Predicate<Character> IS_LETTER = new Predicate<Character>() {
        public boolean matches(Character c) {
            return Character.isLetter(c);
        }

        @Override
        public String toString() {
            return "letter";
        }
    };

    /**
     * A {@link Predicate<Character>} that returns true if it is an alphanumeric character, or an
     * underscore character.
     */
    public static final Predicate<Character> IS_ALPHA_NUMERIC = new Predicate<Character>() {
        public boolean matches(Character c) {
            return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
        }

        @Override
        public String toString() {
            return "[0-9a-zA-Z]";
        }
    };

    /**
     * A {@link Predicate<Character>} that returns true if it is an alphanumeric character, or an
     * underscore character.
     */
    public static final Predicate<Character> IS_ALPHA_NUMERIC_ = new Predicate<Character>() {
        public boolean matches(Character c) {
            return c == '_' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
        }

        @Override
        public String toString() {
            return "[0-9a-zA-Z_]";
        }
    };

    /**
     * A {@link Predicate<Character>} that returns true if the character is equal to {@code c}.
     */
    public static Predicate<Character> isChar(final char c) {
        return new Predicate<Character>() {
            public boolean matches(Character x) {
                return x == c;
            }

            @Override
            public String toString() {
                return Character.toString(c);
            }
        };
    }

    /**
     * A {@link Predicate<Character>} that returns true if the character is not equal to {@code c}.
     */
    public static Predicate<Character> notChar(final char c) {
        return new Predicate<Character>() {
            public boolean matches(Character x) {
                return x != c;
            }

            @Override
            public String toString() {
                return "^" + Character.toString(c);
            }
        };
    }

    /**
     * A {@link Predicate<Character>} that returns true if the character is within the range of
     * {@code [a, b]}.
     */
    public static Predicate<Character> range(final char a, final char b) {
        return new Predicate<Character>() {
            public boolean matches(Character c) {
                return c >= a && c <= b;
            }

            @Override
            public String toString() {
                return "[" + a + '-' + b + "]";
            }
        };
    }

    /**
     * A {@link Predicate<Character>} that returns true if the character is a digit.
     */
    public static final Predicate<Character> IS_DIGIT = range('0', '9');

    /**
     * A {@link Predicate<Character>} that returns true if the character is not within the range of
     * {@code [a, b]}.
     */
    public static Predicate<Character> notRange(final char a, final char b) {
        return new Predicate<Character>() {
            public boolean matches(Character c) {
                return !(c >= a && c <= b);
            }

            @Override
            public String toString() {
                return "[^" + a + '-' + b + "]";
            }
        };
    }

    /**
     * A {@link Predicate<Character>} that returns true if the character is equal to any character in
     * {@code chars}.
     */
    public static Predicate<Character> among(final String chars) {
        return new Predicate<Character>() {
            public boolean matches(Character c) {
                return chars.indexOf(c) >= 0;
            }

            @Override
            public String toString() {
                return '[' + chars + ']';
            }
        };
    }

    /**
     * A {@link Predicate<Character>} that returns true if the character is not equal to any character
     * in {@code chars}.
     */
    public static Predicate<Character> notAmong(final String chars) {
        return new Predicate<Character>() {
            public boolean matches(Character c) {
                return chars.indexOf(c) < 0;
            }

            @Override
            public String toString() {
                return "^[" + chars + ']';
            }
        };
    }

    /**
     * A {@link Predicate<Character>} that returns true if {@code predicate} evaluates to false.
     */
    public static Predicate<Character> not(final Predicate<Character> predicate) {
        return new Predicate<Character>() {
            public boolean matches(Character c) {
                return !predicate.matches(c);
            }

            @Override
            public String toString() {
                return "^" + predicate;
            }
        };
    }

    /**
     * A {@link Predicate<Character>} that returns true if both {@code predicate1} and
     * {@code predicate2} evaluates to true.
     */
    public static Predicate<Character> and(final Predicate<Character> predicate1, final Predicate<Character> predicate2) {
        return new Predicate<Character>() {
            public boolean matches(Character c) {
                return predicate1.matches(c) && predicate2.matches(c);
            }

            @Override
            public String toString() {
                return predicate1 + " and " + predicate2;
            }
        };
    }

    /**
     * A {@link Predicate<Character>} that returns true if either {@code predicate1} or
     * {@code predicate2} evaluates to true.
     */
    public static Predicate<Character> or(final Predicate<Character> predicate1, final Predicate<Character> predicate2) {
        return new Predicate<Character>() {
            public boolean matches(Character c) {
                return predicate1.matches(c) || predicate2.matches(c);
            }

            @Override
            public String toString() {
                return predicate1 + " or " + predicate2;
            }
        };
    }

    /**
     * A {@link Predicate<Character>} that returns true if all {@code Predicate<Character>} in
     * {@code predicates} evaluate to true.
     */
    public static Predicate<Character> and(final Predicate<Character>... predicates) {
        if (predicates.length == 0)
            return ALWAYS;
        else if (predicates.length == 1) return predicates[0];
        return new Predicate<Character>() {
            public boolean matches(Character c) {
                for (int i = 0; i < predicates.length; i++) {
                    if (!predicates[i].matches(c)) return false;
                }
                return true;
            }

            @Override
            public String toString() {
                return Strings.join(" and ", predicates);
            }
        };
    }

    /**
     * A {@link Predicate<Character>} that returns true if any {@code Predicate<Character>} in
     * {@code predicates} evaluates to true.
     */
    public static Predicate<Character> or(final Predicate<Character>... predicates) {
        if (predicates.length == 0)
            return NEVER;
        else if (predicates.length == 1) return predicates[0];
        return new Predicate<Character>() {
            public boolean matches(Character c) {
                for (int i = 0; i < predicates.length; i++) {
                    if (predicates[i].matches(c)) return true;
                }
                return false;
            }

            @Override
            public String toString() {
                return Strings.join(" or ", predicates);
            }
        };
    }
}
