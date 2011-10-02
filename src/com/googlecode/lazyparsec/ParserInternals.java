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

import java.util.Collection;

/**
 * Common internal utilities.
 *
 * @author benyu
 */
final class ParserInternals {

    static void runForBestFit(
            IntOrder order, Parser<?>[] parsers, int from,
            ParseContext state,
            Object originalResult, int originalStep, int originalAt) {
        int bestAt = state.at;
        int bestStep = state.step;
        Object bestResult = state.result;
        for (int i = from; i < parsers.length; i++) {
            state.set(originalStep, originalAt, originalResult);
            Parser<?> parser = parsers[i];
            boolean ok = parser.run(state);
            if (!ok) continue;
            int at2 = state.at;
            if (order.compare(at2, bestAt)) {
                bestAt = at2;
                bestStep = state.step;
                bestResult = state.result;
            }
        }
        state.set(bestStep, bestAt, bestResult);
    }

    static boolean repeat(Parser<?> parser, int n, ParseContext context) {
        for (int i = 0; i < n; i++) {
            if (!parser.run(context)) return false;
        }
        return true;
    }

    static boolean many(final Parser<?> parser, final ParseContext context) {
        for (int at = context.at, step = context.step; ; step = context.step) {
            if (!greedyRun(parser, context)) return stillThere(context, at, step);
            int at2 = context.at;
            if (at == at2) return true;
            at = at2;
        }
    }

    static boolean repeatAtMost(Parser<?> parser, int max, ParseContext context) {
        for (int i = 0; i < max; i++) {
            int at = context.at;
            int step = context.step;
            if (!greedyRun(parser, context)) return stillThere(context, at, step);
        }
        return true;
    }

    static <T> boolean repeat(
            Parser<? extends T> parser, int n, Collection<T> collection, ParseContext context) {
        for (int i = 0; i < n; i++) {
            if (!parser.run(context)) return false;
            collection.add(parser.getReturn(context));
        }
        return true;
    }

    static <T> boolean repeatAtMost(
            Parser<? extends T> parser, int max, Collection<T> collection, ParseContext context) {
        for (int i = 0; i < max; i++) {
            int at = context.at;
            int step = context.step;
            if (!greedyRun(parser, context)) return stillThere(context, at, step);
            collection.add(parser.getReturn(context));
        }
        return true;
    }

    static <T> boolean many(
            Parser<? extends T> parser, Collection<T> collection, ParseContext context) {
        for (int at = context.at, step = context.step; ; step = context.step) {
            if (!greedyRun(parser, context)) return stillThere(context, at, step);
            int at2 = context.at;
            if (at == at2) return true;
            at = at2;
            collection.add(parser.getReturn(context));
        }
    }

    static boolean stillThere(ParseContext context, int wasAt, int originalStep) {
        if (context.step == originalStep) {
            // logical step didn't change, so logically we are still there, undo any physical offset
            context.setAt(originalStep, wasAt);
            return true;
        }
        return false;
    }

    static boolean runNestedParser(
            ParseContext context, ParseContext freshInitState, Parser<?> parser) {
        if (parser.run(freshInitState)) {
            context.set(freshInitState.step, context.at, freshInitState.result);
            return true;
        }
        // index on token level is the "at" on character level
        context.set(context.step, freshInitState.getIndex(), null);

        // always copy error because there could be false alarms in the character level.
        // For example, a "or" parser nested in a "many" failed in one of its branches.
        copyError(context, freshInitState);
        return false;
    }

    private static void copyError(ParseContext context, ParseContext nestedState) {
        int errorIndex = nestedState.errorIndex();
        context.setErrorState(
                errorIndex, errorIndex, nestedState.errorType(), nestedState.errors());
        if (!nestedState.isEof()) {
            context.setEncountered(nestedState.getEncountered());
        }
    }

    /**
     * Runs {@code parser} in greedy mode. Currently it does nothing special.
     * May want to suppress irrelevant errors (such the 'x expected' in x*).
     */
    static boolean greedyRun(Parser<?> parser, ParseContext context) {
        return parser.run(context);
    }

    /**
     * Runs {@code parser} with error recording suppressed.
     */
    static boolean runWithoutRecordingError(Parser<?> parser, ParseContext context) {
        boolean oldValue = context.suppressError(true);
        boolean ok = parser.run(context);
        context.suppressError(oldValue);
        return ok;
    }
}
