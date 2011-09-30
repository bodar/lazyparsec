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

import com.googlecode.lazyparsec.Tokens.Fragment;
import com.googlecode.lazyparsec.Tokens.ScientificNotation;
import com.googlecode.lazyparsec.Tokens.Tag;
import com.googlecode.totallylazy.Callable1;

/**
 * Common {@link com.googlecode.totallylazy.Callable1} implementations that maps from {@link String}.
 * 
 * @author Ben Yu
 */
final class TokenizerMaps {

  /** A {@link com.googlecode.totallylazy.Callable1} that returns a {@link Tokens.Fragment} tagged as {@link Tag#RESERVED}. */
  static final Callable1<String, Fragment> RESERVED_FRAGMENT = fragment(Tag.RESERVED);
  
  /** A {@link com.googlecode.totallylazy.Callable1} that returns a {@link Tokens.Fragment} tagged as {@link Tag#IDENTIFIER}. */
  static final Callable1<String, Fragment> IDENTIFIER_FRAGMENT = fragment(Tag.IDENTIFIER);
  
  /** A {@link com.googlecode.totallylazy.Callable1} that returns a {@link Tokens.Fragment} tagged as {@link Tag#INTEGER}. */
  static final Callable1<String, Fragment> INTEGER_FRAGMENT = fragment(Tag.INTEGER);
  
  /** A {@link com.googlecode.totallylazy.Callable1} that returns a {@link Tokens.Fragment} tagged as {@link Tag#DECIMAL}. */
  static final Callable1<String, Fragment> DECIMAL_FRAGMENT = fragment(Tag.DECIMAL);
  
  /**
   * A {@link com.googlecode.totallylazy.Callable1} that recognizes a scientific notation
   * and tokenizes to a {@link ScientificNotation}.
   */
  static final Callable1<String, ScientificNotation> SCIENTIFIC_NOTATION =
      new Callable1<String, ScientificNotation>() {
        public ScientificNotation call(String text) {
          int e = text.indexOf('e');
          if (e < 0) {
            e = text.indexOf('E');
          }
          // we know for sure the string is in expected format, so don't bother checking.
          String significand = text.substring(0, e);
          String exponent = text.substring(e + (text.charAt(e + 1) == '+' ? 2 : 1), text.length());
          return Tokens.scientificNotation(significand, exponent);
        }
        @Override public String toString() {
          return "SCIENTIFIC_NOTATION";
        }
      };
  
  /**
   * A {@link com.googlecode.totallylazy.Callable1} that recognizes a string literal quoted by double quote character
   * ({@code "}) and tokenizes to a {@code String}. The backslash character ({@code \}) is
   * interpreted as escape.
   */
  static final Callable1<String, String> DOUBLE_QUOTE_STRING = new Callable1<String, String>() {
    public String call(String text) {
      return StringLiteralsTranslator.tokenizeDoubleQuote(text);
    }
    @Override public String toString() {
      return "DOUBLE_QUOTE_STRING";
    }
  };

  /**
   * A {@link com.googlecode.totallylazy.Callable1} that tokenizes a SQL style string literal quoted by single quote character
   * ({@code '}) and tokenizes to a {@code String}. Two adjacent single quote characters
   * ({@code ''}) are escaped as one single quote character.
   */
  static final Callable1<String, String> SINGLE_QUOTE_STRING = new Callable1<String, String>() {
    public String call(String text) {
      return StringLiteralsTranslator.tokenizeSingleQuote(text);
    }
    @Override public String toString() {
      return "SINGLE_QUOTE_STRING";
    }
  };
  
  /**
   * A {@link com.googlecode.totallylazy.Callable1} that recognizes a character literal quoted by single quote characte
   * ({@code '} and tokenizes to a {@link Character}. The backslash character ({@code \}) is
   * interpreted as escape. 
   */
  static final Callable1<String, Character> SINGLE_QUOTE_CHAR = new Callable1<String, Character>() {
    public Character call(String text) {
      int len = text.length();
      if (len == 3) return text.charAt(1);
      else if (len == 4) return text.charAt(2);
      throw new IllegalStateException("illegal char");
    }
    @Override public String toString() {
      return "SINGLE_QUOTE_CHAR";
    }
  };
  
  /**
   * A {@link com.googlecode.totallylazy.Callable1} that interprets the recognized character range
   * as a decimal integer and tokenizes to a {@link Long}.
   */
  static final Callable1<String, Long> DEC_AS_LONG = new Callable1<String, Long>() {
    public Long call(String text) {
      return NumberLiteralsTranslator.tokenizeDecimalAsLong(text);
    }
    @Override public String toString() {
      return "DEC_AS_LONG";
    }
  };
  
  /**
   * A {@link com.googlecode.totallylazy.Callable1} that interprets the recognized character range
   * as a octal integer and tokenizes to a {@link Long}.
   */
  static final Callable1<String, Long> OCT_AS_LONG = new Callable1<String, Long>() {
    public Long call(String text) {
      return NumberLiteralsTranslator.tokenizeOctalAsLong(text);
    }
    @Override public String toString() {
      return "OCT_AS_LONG";
    }
  };
  
  /**
   * A {@link com.googlecode.totallylazy.Callable1} that interprets the recognized character range
   * as a hexadecimal integer and tokenizes to a {@link Long}.
   */
  static final Callable1<String, Long> HEX_AS_LONG = new Callable1<String, Long>() {
    public Long call(String text) {
      return NumberLiteralsTranslator.tokenizeHexAsLong(text);
    }
    @Override public String toString() {
      return "HEX_AS_LONG";
    }
  };
  
  /**
   * Returns a map that tokenizes the recognized character range to a
   * {@link Tokens.Fragment} object tagged with {@code tag}.
   */
  static Callable1<String, Fragment> fragment(final Object tag) {
    return new Callable1<String, Fragment>() {
      public Fragment call(String text) {
        return Tokens.fragment(text, tag);
      }
      @Override public String toString() {
        return String.valueOf(tag);
      }
    };
  }
}
