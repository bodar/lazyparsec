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
import com.googlecode.lazyparsec.util.Strings;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callers;

import static com.googlecode.lazyparsec.util.Checks.checkArgument;

/**
 * A {@link Lexicon} is a group of lexical words that can be tokenized by a single tokenizer.
 *
 * @author Ben Yu
 */
class Lexicon {

    /**
     * Maps lexical word name to token value.
     */
    final Callable1<String, Object> words;

    /**
     * The scanner that recognizes any of the lexical word.
     */
    final Parser<?> tokenizer;

    Lexicon(Callable1<String, Object> words, Parser<?> tokenizer) {
        this.words = words;
        this.tokenizer = tokenizer;
    }

    /**
     * Returns the tokenizer that tokenizes all managed terminals.
     */
    @SuppressWarnings("unchecked")
    public Parser<Object> tokenizer() {
        return (Parser) tokenizer;
    }

    /**
     * A {@link Parser} that recognizes a sequence of tokens identified by {@code tokenNames}, as an
     * atomic step.
     */
    public Parser<Object> phrase(String... tokenNames) {
        Parser<?>[] wordParsers = new Parser<?>[tokenNames.length];
        for (int i = 0; i < tokenNames.length; i++) {
            wordParsers[i] = token(tokenNames[i]);
        }
        return Parsers.sequence(wordParsers).atomic()
                .label(Strings.join(" ", tokenNames));
    }

    /**
     * A {@link Parser} that recognizes a token identified by any of {@code tokenNames}.
     */
    public Parser<Token> token(String... tokenNames) {
        if (tokenNames.length == 0) return Parsers.never();
        @SuppressWarnings("unchecked")
        Parser<Token>[] ps = new Parser[tokenNames.length];
        for (int i = 0; i < tokenNames.length; i++) {
            ps[i] = Parsers.token(InternalFunctors.tokenWithSameValue(word(tokenNames[i])));
        }
        return Parsers.plus(ps);
    }

    /**
     * A {@link Parser} that recognizes the token identified by {@code tokenName}.
     */
    public Parser<Token> token(String tokenName) {
        return Parsers.token(InternalFunctors.tokenWithSameValue(word(tokenName)));
    }

    /**
     * Gets the token value identified by the token text. This text is the operator or the keyword.
     *
     * @param name the token text.
     * @return the token object.
     * @throws IllegalArgumentException if the token object does not exist.
     */
    @Private
    Object word(String name) {
        Object p = Callers.call(words, name);
        checkArgument(p != null, "token %s unavailable", name);
        return p;
    }

    /**
     * Returns a {@link Lexicon} instance that's a union of {@code this} and {@code that}.
     */
    Lexicon union(Lexicon that) {
        return new Lexicon(
                InternalFunctors.fallback(words, that.words),
                Parsers.plus(tokenizer, that.tokenizer));
    }
}
