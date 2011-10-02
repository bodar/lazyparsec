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

import com.googlecode.totallylazy.Callable2;

final class Sequence2Parser<A, B, T> extends Parser<T> {
    private final Parser<A> p1;
    private final Parser<B> p2;
    private final Callable2<? super A, ? super B, ? extends T> m2;

    Sequence2Parser(Parser<A> p1, Parser<B> p2, Callable2<? super A, ? super B, ? extends T> m2) {
        this.p1 = p1;
        this.p2 = p2;
        this.m2 = m2;
    }

    @Override
    boolean apply(ParseContext context) throws Exception {
        boolean r1 = p1.run(context);
        if (!r1) return false;
        A o1 = p1.getReturn(context);
        boolean r2 = p2.run(context);
        if (!r2) return false;
        B o2 = p2.getReturn(context);
        context.result = m2.call(o1, o2);
        return true;
    }

    @Override
    public String toString() {
        return m2.toString();
    }
}