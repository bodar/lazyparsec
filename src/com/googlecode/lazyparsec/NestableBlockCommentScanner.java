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


/**
 * Parses any nestable comment pattern.
 *
 * @author Ben Yu
 */
final class NestableBlockCommentScanner extends Parser<Void> {
    private final Parser<?> openQuote;
    private final Parser<?> closeQuote;
    private final Parser<?> commented;

    NestableBlockCommentScanner(Parser<?> openQuote, Parser<?> closeQuote, Parser<?> commented) {
        this.openQuote = openQuote;
        this.closeQuote = closeQuote;
        this.commented = commented;
    }

    @Override
    boolean apply(final ParseContext context) {
        if (!openQuote.run(context)) return false;
        for (int level = 1; level > 0; ) {
            final int step = context.step;
            final int at = context.at;
            if (closeQuote.run(context)) {
                if (at == context.at) {
                    throw new IllegalStateException("closing comment scanner not consuming input.");
                }
                level--;
                continue;
            }
            if (!ParserInternals.stillThere(context, at, step)) return false;
            if (openQuote.run(context)) {
                if (at == context.at) {
                    throw new IllegalStateException("opening comment scanner not consuming input.");
                }
                level++;
                continue;
            }
            if (!ParserInternals.stillThere(context, at, step)) return false;
            if (commented.run(context)) {
                if (at == context.at) {
                    throw new IllegalStateException("commented scanner not consuming input.");
                }
                continue;
            }
            return false;
        }
        context.result = null;
        return true;
    }

    @Override
    public String toString() {
        return "nestable block comment";
    }
}