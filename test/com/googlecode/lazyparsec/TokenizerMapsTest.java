package com.googlecode.lazyparsec;

import com.googlecode.totallylazy.Callable1;
import junit.framework.TestCase;

import com.googlecode.lazyparsec.Tokens.Fragment;
import com.googlecode.lazyparsec.Tokens.Tag;

/**
 * Unit test for {@link TokenizerMaps}.
 * 
 * @author Ben Yu
 */
public class TokenizerMapsTest extends TestCase {
  
  public void testFragment() throws Exception {
    assertFragment("foo", TokenizerMaps.fragment("foo"));
  }
  
  public void testReservedFragment() throws Exception {
    assertFragment(Tag.RESERVED, TokenizerMaps.RESERVED_FRAGMENT);
  }
  
  public void testIdentifierFragment() throws Exception {
    assertFragment(Tag.IDENTIFIER, TokenizerMaps.IDENTIFIER_FRAGMENT);
  }
  
  public void testIntegerFragment() throws Exception {
    assertFragment(Tag.INTEGER, TokenizerMaps.INTEGER_FRAGMENT);
  }
  
  public void testDecimalFragment() throws Exception {
    assertFragment(Tag.DECIMAL, TokenizerMaps.DECIMAL_FRAGMENT);
  }
  
  public void testSingleQuoteChar() throws Exception {
    assertEquals("SINGLE_QUOTE_CHAR", TokenizerMaps.SINGLE_QUOTE_CHAR.toString());
    assertEquals(Character.valueOf('a'), TokenizerMaps.SINGLE_QUOTE_CHAR.call("'a'"));
    assertEquals(Character.valueOf('a'), TokenizerMaps.SINGLE_QUOTE_CHAR.call("'\\a'"));
    try {
      TokenizerMaps.SINGLE_QUOTE_CHAR.call("'abc'");
      fail();
    } catch (IllegalStateException e) {}
  }
  
  public void testDecAsLong() throws Exception {
    assertEquals("DEC_AS_LONG", TokenizerMaps.DEC_AS_LONG.toString());
    assertEquals(Long.valueOf(123L), TokenizerMaps.DEC_AS_LONG.call("123"));
  }
  
  public void testOctAsLong() throws Exception {
    assertEquals("OCT_AS_LONG", TokenizerMaps.OCT_AS_LONG.toString());
    assertEquals(Long.valueOf(10L), TokenizerMaps.OCT_AS_LONG.call("012"));
  }
  
  public void testHexAsLong() throws Exception {
    assertEquals("HEX_AS_LONG", TokenizerMaps.HEX_AS_LONG.toString());
    assertEquals(Long.valueOf(255L), TokenizerMaps.HEX_AS_LONG.call("0xff"));
  }
  
  public void testDoubleQuoteString() throws Exception {
    assertEquals("DOUBLE_QUOTE_STRING", TokenizerMaps.DOUBLE_QUOTE_STRING.toString());
    assertEquals("c:\\home", TokenizerMaps.DOUBLE_QUOTE_STRING.call("\"c:\\\\home\""));
  }
  
  public void testSingleQuoteString() throws Exception {
    assertEquals("SINGLE_QUOTE_STRING", TokenizerMaps.SINGLE_QUOTE_STRING.toString());
    assertEquals("'a'", TokenizerMaps.SINGLE_QUOTE_STRING.call("'''a'''"));
  }
  
  public void testScientificNotation() throws Exception {
    assertEquals("SCIENTIFIC_NOTATION", TokenizerMaps.SCIENTIFIC_NOTATION.toString());
    assertEquals(new Tokens.ScientificNotation("1", "2"),
        TokenizerMaps.SCIENTIFIC_NOTATION.call("1e2"));
    assertEquals(new Tokens.ScientificNotation("1", "2"),
        TokenizerMaps.SCIENTIFIC_NOTATION.call("1e+2"));
    assertEquals(new Tokens.ScientificNotation("1", "-2"),
        TokenizerMaps.SCIENTIFIC_NOTATION.call("1e-2"));
    assertEquals(new Tokens.ScientificNotation("1.2", "30"),
        TokenizerMaps.SCIENTIFIC_NOTATION.call("1.2E30"));
    assertEquals(new Tokens.ScientificNotation("0", "0"),
        TokenizerMaps.SCIENTIFIC_NOTATION.call("0E0"));
  }

  private void assertFragment(Object tag, Callable1<String, Fragment> callable1) throws Exception {
    Fragment fragment = callable1.call("foo");
    assertEquals(tag, fragment.tag());
    assertEquals("foo", fragment.text());
    assertEquals(tag.toString(), callable1.toString());
  }
}
