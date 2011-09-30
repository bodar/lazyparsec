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

import static com.googlecode.lazyparsec.Asserts.assertFailure;
import static com.googlecode.lazyparsec.Scanners.isChar;
import static com.googlecode.lazyparsec.TestParsers.areChars;

import java.util.List;

import junit.framework.TestCase;

import com.googlecode.lazyparsec.functors.Unary;
import com.googlecode.lazyparsec.util.Lists;

/**
 * Unit test for error handling of {@link Parser}.
 * 
 * @author benyu
 */
public class ParserErrorHandlingTest extends TestCase {
  
  public void testNotOverridesNever() {
    assertError(
        isChar('a').next(isChar('x').not()), isChar('a').next(Parsers.never()),
        "ax", 1, 2, "unexpected x.");
  }
  
  public void testExpectOverridesNot() {
    assertError(
        areChars("ab"), isChar('a').next(isChar('x').not()),
        "ax", 1, 2, "b expected, x encountered.");
  }
  
  public void testFailureOverridesExpect() {
    assertError(
        areChars("ab"), isChar('a').next(Parsers.fail("foo")),
        "ax", 1, 2, "foo");
  }
  
  public void testFailureOverridesExplicitExpect() {
    assertError(
        isChar('a').next(Parsers.fail("bar")), isChar('a').next(Parsers.expect("foo")),
        "ax", 1, 2, "bar");
  }
  
  public void testMoreRelevantErrorWins() {
    assertError(areChars("abc"), isChar('a').next(Parsers.expect("foo")), "ab",
        1, 3, "c expected, EOF encountered.");
  }
  
  public void testFirstNeverWins() {
    assertError(Parsers.never(), Parsers.never(), "x", 1, 1, "");
  }
  
  public void testFirstNotWins() {
    assertFailure(
        Parsers.or(isChar('x').not("xxx"), isChar('x').not("X")), "x", 1, 1, "unexpected xxx.");
  }
  
  public void testFirstFailureWins() {
    assertFailure(
        Parsers.or(Parsers.fail("foo"), Parsers.fail("bar")), "x", 1, 1, "foo");
  }
  
  public void testExpectMerged() {
    assertFailure(
        Parsers.or(Parsers.expect("foo"), Parsers.expect("bar")), "x",
        1, 1, "foo or bar expected, x encountered.");
    assertFailure(
        Parsers.or(Parsers.expect("foo").label("foo"), Parsers.expect("foo")), "x",
        1, 1, "foo expected, x encountered.");
  }
  
  public void testExpectedMerged() {
    assertFailure(
        Parsers.or(isChar('a'), isChar('b')), "x",
        1, 1, "a or b expected, x encountered.");
  }
  
  public void testErrorSurvivesPlus() {
    assertError(
        Parsers.plus(areChars("abc").atomic(), isChar('a')).next(isChar('x')), areChars("ax"),
        "abx", 1, 3, "c expected, x encountered.");
  }
  
  public void testErrorSurvivesOr() {
    assertError(
        Parsers.or(areChars("abc"), isChar('a')).next(isChar('x')), areChars("ax"),
        "abx", 1, 3, "c expected, x encountered.");
  }
  
  public void testErrorSurvivesLonger() {
    assertError(
        Parsers.longer(areChars("abc"), isChar('a')).next(isChar('x')), areChars("ax"),
        "abx", 1, 3, "c expected, x encountered.");
  }
  
  public void testErrorSurvivesShorter() {
    assertError(
        Parsers.shorter(areChars("abc"), isChar('a')).next(isChar('x')), areChars("ax"),
        "abx", 1, 3, "c expected, x encountered.");
  }
  
  public void testErrorSurvivesRepetition() {
    assertError(
        areChars("abc").many(), areChars("ax"), "abx", 1, 3, "c expected, x encountered.");
    assertError(
        areChars("abc").skipMany(), areChars("ax"), "abx", 1, 3, "c expected, x encountered.");
    assertError(
        areChars("abc").many1(), areChars("ax"), "abx", 1, 3, "c expected, x encountered.");
    assertError(
        areChars("abc").skipMany1(), areChars("ax"), "abx", 1, 3, "c expected, x encountered.");
    assertError(
        areChars("abc").times(0, 1), areChars("ax"), "abx", 1, 3, "c expected, x encountered.");
    assertError(
        areChars("abc").skipTimes(0, 1), areChars("ax"), "abx", 1, 3, "c expected, x encountered.");
    assertError(
        areChars("abc").times(0, 2), areChars("ax"), "abx", 1, 3, "c expected, x encountered.");
    assertError(
        areChars("abc").skipTimes(0, 2), areChars("ax"), "abx", 1, 3, "c expected, x encountered.");
    assertError(
        areChars("abc").times(1), areChars("ax"), "abx", 1, 3, "c expected, x encountered.");
    assertError(
        areChars("abc").skipTimes(1), areChars("ax"), "abx", 1, 3, "c expected, x encountered.");
  }
  
  public void testOuterExpectWins() {
    assertFailure(Parsers.expect("foo").label("bar"), "", 1, 1, "bar expected, EOF encountered.");
  }
  
  public void testTokenLevelError() {
    Terminals terminals =
        Terminals.caseSensitive(new String[] {"+", "-"}, new String[] {"foo", "bar", "baz"});
    Parser<?> lexer = terminals.tokenizer();
    Parser<List<Token>> foobar =
        terminals.token("foo", "bar").times(2).from(lexer, Scanners.WHITESPACES);
    assertFailure(foobar, "foo+", 1, 4, "foo or bar expected, + encountered.");
    assertFailure(foobar, "foo", 1, 4, "foo or bar expected, EOF encountered.");
    assertFailure(Parsers.or(areChars("foo bar"), foobar), "foo baz",
        1, 5, "foo or bar expected, baz encountered.");
    assertFailure(Parsers.or(foobar, areChars("foo bar")), "foo baz",
        1, 7, "r expected, z encountered.");
    assertFailure(Parsers.or(areChars("foox"), foobar), "foo baz",
        1, 5, "foo or bar expected, baz encountered.");
    Parser<List<Token>> foobar2 =
      terminals.token("foo", "bar").times(2).from(lexer.next(lexer), Scanners.WHITESPACES);
    assertError(foobar2, areChars("foox"), "+foo -baz",
        1, 6, "foo or bar expected, baz encountered.");
  }
  
  public void testEmptyTokenCounts() {
    Terminals terminals =
      Terminals.caseSensitive(new String[] {"+", "-"}, new String[] {"foo", "bar", "baz"});
    Parser<List<Token>> lexeme = terminals.tokenizer().lexer(Scanners.WHITESPACES)
        .map(new Unary<List<Token>>() {
          public List<Token> call(List<Token> tokens) {
            List<Token> result = Lists.arrayList();
            for (Token token : tokens) {
              result.add(new Token(token.index(), 0, "("));
              result.add(token);
              int index = token.index() + token.length();
              result.add(new Token(index, 0, ")"));
            }
            return result;
          }
        });
    Parser<Token> LPAREN = Parsers.token(InternalFunctors.tokenWithSameValue("("));
    Parser<Token> RPAREN = Parsers.token(InternalFunctors.tokenWithSameValue(")"));
    Parser<?> parser = Parsers.or(
        Parsers.sequence(LPAREN, terminals.token("foo"), terminals.token("bar")),
        Parsers.sequence(LPAREN, terminals.token("foo"), RPAREN, terminals.token("bar")));
    assertFailure(parser.from(lexeme), "foo baz", 1, 5, "bar expected, ( encountered.");
  }
  
  private static void assertError(
      Parser<?> a, Parser<?> b, String source, int line, int column, String message) {
    assertFailure(
        Parsers.or(a, b), source, line, column, message);
    assertFailure(
        Parsers.or(b, a), source, line, column, message);
    assertFailure(
        Parsers.longer(a, b), source, line, column, message);
    assertFailure(
        Parsers.longer(b, a), source, line, column, message);
    assertFailure(
        Parsers.shorter(a, b), source, line, column, message);
    assertFailure(
        Parsers.shorter(b, a), source, line, column, message);
  }
}
