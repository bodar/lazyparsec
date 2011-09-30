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

import com.googlecode.lazyparsec.functors.Map2;
import com.googlecode.lazyparsec.functors.Map3;
import com.googlecode.lazyparsec.functors.Map4;
import com.googlecode.lazyparsec.functors.Map5;
import com.googlecode.totallylazy.Callable1;

/**
 * Functors used only internally by this package. 
 * 
 * @author Ben Yu
 */
final class InternalFunctors {
  
  static <T> TokenMap<T> isTokenType(final Class<T> targetType, final String name) {
    return new TokenMap<T>() {
      public T map(Token token) {
        if (targetType.isInstance(token.value())) {
          return targetType.cast(token.value());
        }
        return null;
      }
      @Override public String toString() {
        return name;
      }
    };
  }
  
  static TokenMap<Token> tokenWithSameValue(final Object value) {
    return new TokenMap<Token>() {
      public Token map(Token token) {
        return (value == token.value()) ? token : null;
      }
      @Override public String toString() {
        return String.valueOf(value);
      }
    };
  }
  
  @SuppressWarnings("unchecked")
  private static final Map2 FIRST_OF_TWO = new Map2() {
    public Object map(Object first, Object b) {
      return first;
    }
    @Override public String toString() {
      return "followedBy";
    }
  };
  
  @SuppressWarnings("unchecked")
  private static final Map2 LAST_OF_TWO = new Map2() {
    public Object map(Object a, Object last) {
      return last;
    }
    @Override public String toString() {
      return "sequence";
    }
  };
  
  @SuppressWarnings("unchecked")
  private static final Map3 LAST_OF_THREE = new Map3() {
    public Object map(Object a, Object b, Object last) {
      return last;
    }
    @Override public String toString() {
      return "sequence";
    }
  };
  
  @SuppressWarnings("unchecked")
  private static final Map4 LAST_OF_FOUR = new Map4() {
    public Object map(Object a, Object b, Object c, Object last) {
      return last;
    }
    @Override public String toString() {
      return "sequence";
    }
  };
  
  @SuppressWarnings("unchecked")
  private static final Map5 LAST_OF_FIVE = new Map5() {
    public Object map(Object a, Object b, Object c, Object d, Object last) {
      return last;
    }
    @Override public String toString() {
      return "sequence";
    }
  };
  
  @SuppressWarnings("unchecked")
  static <T, B> Map2<T, B, T> firstOfTwo() {
    return FIRST_OF_TWO;
  }
  
  @SuppressWarnings("unchecked")
  static<A, T> Map2<A, T, T> lastOfTwo() {
    return LAST_OF_TWO;
  }
  
  @SuppressWarnings("unchecked")
  static<A, B, T> Map3<A, B, T, T> lastOfThree() {
    return LAST_OF_THREE;
  }
  
  @SuppressWarnings("unchecked")
  static<A, B, C, T> Map4<A, B, C, T, T> lastOfFour() {
    return LAST_OF_FOUR;
  }
  
  @SuppressWarnings("unchecked")
  static<A, B, C, D, T> Map5<A, B, C, D, T, T> lastOfFive() {
    return LAST_OF_FIVE;
  }

  /**
   * Returns a {@link com.googlecode.totallylazy.Callable1} that delegates to {@code map} and falls back to {@code defaultMap} for
   * null return values.
   */
  public static <F, T> Callable1<F, T> fallback(
      final Callable1<F, T> callable1, final Callable1<? super F, ? extends T> defaultCallable1) {
    return new Callable1<F, T>() {
      public T call(F v) throws Exception {
        T result = callable1.call(v);
        return (result == null) ? defaultCallable1.call(v) : result;
      }
      @Override public String toString() {
        return "fallback";
      }
    };
  }
}
