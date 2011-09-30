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

import com.googlecode.totallylazy.Callable1;

/**
 * Runs a {@link Parser} and then maps the parser result to another {@code Parser} object, the
 * returned {@code Parser} object is then executed as the next step.
 * 
 * @author Ben Yu
 */
final class BindNextParser<From, To> extends Parser<To> {
  private final Parser<? extends From> parser;
  private final Callable1<? super From, ? extends Parser<? extends To>> callable1;
  
  BindNextParser(
      Parser<? extends From> parser, Callable1<? super From, ? extends Parser<? extends To>> next) {
    this.callable1 = next;
    this.parser = parser;
  }

  @Override boolean apply(ParseContext ctxt) {
    if (!parser.run(ctxt))
      return false;
    return Parsers.runNext(ctxt, callable1);
  }
  
  @Override public String toString() {
    return callable1.toString();
  }
}