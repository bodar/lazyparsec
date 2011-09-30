package com.googlecode.lazyparsec;

import static com.googlecode.lazyparsec.Asserts.assertFailure;
import static com.googlecode.lazyparsec.Asserts.assertParser;

import junit.framework.TestCase;

/**
 * Unit test for {@link Parser.Reference}.
 * 
 * @author Ben Yu
 */
public class ParserReferenceTest extends TestCase {
  
  public void testLazy() {
    Parser.Reference<String> ref = Parser.newReference();
    assertNull(ref.get());
    Parser<String> lazyParser = ref.lazy();
    assertEquals("lazy", lazyParser.toString());
    ref.set(Parsers.constant("foo"));
    assertParser(lazyParser, "", "foo");
    ref.set(Parsers.constant("bar"));
    assertParser(lazyParser, "", "bar");
  }
  
  public void testUninitializedLazy() {
    Parser.Reference<String> ref = Parser.newReference();
    assertNull(ref.get());
    assertFailure(ref.lazy(), "", 1, 1, "Uninitialized lazy parser reference");
  }
}
