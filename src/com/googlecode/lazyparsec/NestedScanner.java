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
 * After a scanner succeeds, feeds the recognized character range to a nested scanner.
 *
 * @author Ben Yu
 */
final class NestedScanner extends Parser<Void> {
    private final Parser<?> outer;
    private final Parser<Void> inner;

    NestedScanner(Parser<?> parser, Parser<Void> scanner) {
        this.outer = parser;
        this.inner = scanner;
    }

    @Override
    boolean apply(ParseContext context) {
        int from = context.at;
        if (!outer.run(context)) return false;
        ScannerState scannerState = new ScannerState(
                context.module, context.characters(), from, context.at, context.locator, context.result);
        return ParserInternals.runNestedParser(context, scannerState, inner);
    }

    @Override
    public String toString() {
        return "nested scanner";
    }
}