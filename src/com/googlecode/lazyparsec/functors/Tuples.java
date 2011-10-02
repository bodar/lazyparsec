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
package com.googlecode.lazyparsec.functors;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Quadruple;
import com.googlecode.totallylazy.Quintuple;
import com.googlecode.totallylazy.Triple;

/**
 * Creates {@link Pair} and tuple instances.
 * <p/>
 * <p> These data holders can be used to hold temporary results during parsing so you don't have to
 * create your own data types.
 *
 * @author Ben Yu
 */
public final class Tuples {

    /**
     * Returns a {@link Pair} of 2 objects.
     */
    public static <A, B> Pair<A, B> tuple(A a, B b) {
        return Pair.pair(a, b);
    }

    /**
     * Returns a {@link Triple} of 3 objects.
     */
    public static <A, B, C> Triple<A, B, C> tuple(A a, B b, C c) {
        return new Triple<A, B, C>(a, b, c);
    }

    /**
     * Returns a {@link Quadruple} of 4 objects.
     */
    public static <A, B, C, D> Quadruple<A, B, C, D> tuple(A a, B b, C c, D d) {
        return new Quadruple<A, B, C, D>(a, b, c, d);
    }

    /**
     * Returns a {@link Quintuple} of 5 objects.
     */
    public static <A, B, C, D, E> Quintuple<A, B, C, D, E> tuple(A a, B b, C c, D d, E e) {
        return new Quintuple<A, B, C, D, E>(a, b, c, d, e);
    }
}
