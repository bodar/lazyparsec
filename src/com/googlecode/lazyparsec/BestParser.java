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


final class BestParser<T> extends Parser<T> {
    private final Parser<? extends T>[] parsers;
    private final IntOrder order;

    BestParser(Parser<? extends T>[] parsers, IntOrder order) {
        this.parsers = parsers;
        this.order = order;
    }

    @Override
    boolean apply(ParseContext context) {
        final Object result = context.result;
        final int step = context.step;
        final int at = context.at;
        for (int i = 0; i < parsers.length; i++) {
            Parser<? extends T> parser = parsers[i];
            if (parser.run(context)) {
                ParserInternals.runForBestFit(order, parsers, i + 1, context, result, step, at);
                return true;
            }
            // in alternate, we do not care partial match.
            context.set(step, at, result);
        }
        return false;
    }

    @Override
    public String toString() {
        return order.toString();
    }
}