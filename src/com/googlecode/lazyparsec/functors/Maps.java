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

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Quadruple;
import com.googlecode.totallylazy.Quintuple;
import com.googlecode.totallylazy.Triple;

import java.util.Locale;

/**
 * Provides common implementations of {@link Callable1} interface and the variants.
 * 
 * @author Ben Yu
 */
public final class Maps {
  
  /**
   * The {@link Callable1} that maps a {@link String} to {@link Integer} by calling
   * {@link Integer#valueOf(String)}.
   */
  public static final Callable1<String, Integer> TO_INTEGER = new Callable1<String, Integer>() {
    public Integer call(String v) {
      return Integer.valueOf(v);
    }
    @Override public String toString() {
      return "integer";
    }
  };

  /** The {@link Unary} that maps a {@link String} to lower case using {@link Locale#US}. */
  public static Unary<String> TO_LOWER_CASE = toLowerCase(Locale.US);

  /** Returns a {@link Unary} that maps a {@link String} to lower case using {@code locale}. */
  public static Unary<String> toLowerCase(final Locale locale) {
    return  new Unary<String>() {
      public String call(String s) {
        return s.toLowerCase(locale);
      }
      @Override public String toString() {
        return "toLowerCase";
      }
    };
  }

  /** The {@link Unary} that maps a {@link String} to upper case using {@link Locale#US}. */
  public static Unary<String> TO_UPPER_CASE = toUpperCase(Locale.US);

  /** Returns a {@link Unary} that maps a {@link String} to upper case using {@code locale}. */
  public static Unary<String> toUpperCase(final Locale locale) {
    return  new Unary<String>() {
      public String call(String s) {
        return s.toUpperCase(locale);
      }
      @Override public String toString() {
        return "toUpperCase";
      }
    };
  }
  
  /**
   * A {@link Callable1} instance that maps its parameter to a {@link String} by calling
   * {@link Object#toString()} against it.
   */
  @SuppressWarnings("unchecked")
  public static <T> Callable1<T, String> mapToString() {
    return TO_STRING;
  }
  
  /**
   * Returns a {@link Callable1} that maps the string representation of an enum
   * to the corresponding enum value by calling {@link Enum#valueOf(Class, String)}.
   */
  public static <E extends Enum<E>> Callable1<String, E> toEnum(final Class<E> enumType) {
    return new Callable1<String, E>() {
      public E call(String name) {
        return Enum.valueOf(enumType, name);
      }
      @Override public String toString() {
        return "-> " + enumType.getName();
      }
    };
  }
  
  /** Returns an identity map that maps parameter to itself. */
  @SuppressWarnings("unchecked")
  public static <T> Unary<T> identity() {
    return (Unary<T>) ID;
  }
  
  /** Returns a {@link Callable1} that always maps any object to {@code v}. */
  public static <F, T> Callable1<F, T> constant(final T v) {
    return new Callable1<F, T>() {
      public T call(F from) { return v; }
      @Override public String toString() {
        return String.valueOf(v);
      }
    };
  }
  
  /** Adapts a {@link java.util.Map} to {@link Callable1}. */
  public static <K, V> Callable1<K, V> map(final java.util.Map<K, V> m) {
    return new Callable1<K, V>() {
      public V call(K k) {
        return m.get(k);
      }
      @Override public String toString() {
        return m.toString();
      }
    };
  }
  
  @SuppressWarnings("unchecked")
  private static final Callable2 ID_2 = new Callable2() {
    public Pair call(Object a, Object b) {
        return Pair.pair(a, b);
    }
    @Override public String toString() {
      return "pair";
    }
  };
  
  @SuppressWarnings("unchecked")
  private static final Map3 ID3 = new Map3() {
    public Triple map(Object a, Object b, Object c) {
      return Tuples.tuple(a, b, c);
    }
    @Override public String toString() {
      return "tuple";
    }
  };
  
  @SuppressWarnings("unchecked")
  private static final Map4 ID4 = new Map4() {
    public Quadruple map(Object a, Object b, Object c, Object d) {
      return Tuples.tuple(a, b, c, d);
    }
    @Override public String toString() {
      return "tuple";
    }
  };
      
  @SuppressWarnings("unchecked")
  private static final Map5 ID5 = new Map5() {
    public Quintuple map(Object a, Object b, Object c, Object d, Object e) {
      return Tuples.tuple(a, b, c, d, e);
    }
    @Override public String toString() {
      return "tuple";
    }
  };
  
  /** A {@link Callable2} object that maps 2 values into a {@link Pair} object. */
  @SuppressWarnings("unchecked")
  public static <A, B> Callable2<A, B, Pair<A, B>> toPair() {
    return ID_2;
  }
  
  /** A {@link Map3} object that maps 3 values to a {@link Triple} object. */
  @SuppressWarnings("unchecked")
  public static <A, B, C> Map3<A, B, C, Triple<A, B, C>> toTriple() {
    return ID3;
  }
  
  /** A {@link Map4} object that maps 4 values to a {@link Quadruple} object. */
  @SuppressWarnings("unchecked")
  public static <A, B, C, D> Map4<A, B, C, D, Quadruple<A, B, C, D>> toQuadruple() {
    return ID4;
  }
  
  /** A {@link Map5} object that maps 5 values to a {@link Quintuple} object. */
  @SuppressWarnings("unchecked")
  public static <A, B, C, D, E> Map5<A, B, C, D, E, Quintuple<A, B, C, D, E>> toQuintuple() {
    return ID5;
  }
  
  private static final Unary<Object> ID = new Unary<Object>() {
    public Object call(Object v) {
      return v;
    }
    @Override public String toString() {return "identity";}
  };
  
  @SuppressWarnings("unchecked")
  private static final Callable1 TO_STRING = new Callable1<Object, String>() {
    public String call(Object obj) {
      return String.valueOf(obj);
    }
    @Override public String toString() {
      return "toString";
    }
  };
  
  private Maps() {}
}
