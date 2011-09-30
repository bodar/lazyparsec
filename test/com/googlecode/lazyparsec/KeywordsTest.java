package com.googlecode.lazyparsec;


import static com.googlecode.lazyparsec.Asserts.assertParser;
import junit.framework.TestCase;

/**
 * Unit test for {@link Keywords}.
 * 
 * @author Ben Yu
 */
public class KeywordsTest extends TestCase {
  
  public void testLexicon_caseSensitive() {
    String[] keywords = {"foo", "Bar"};
    Lexicon lexicon = Keywords.lexicon(Scanners.IDENTIFIER, keywords, true, TokenizerMaps.IDENTIFIER_FRAGMENT);
    for (String keyword : keywords) {
      assertEquals(Tokens.reserved(keyword), lexicon.word(keyword));
    }
    for (String keyword : keywords) {
      assertParser(lexicon.tokenizer, keyword, Tokens.reserved(keyword));
    }
    assertParser(lexicon.tokenizer, "FOO", Tokens.identifier("FOO"));
    assertParser(lexicon.tokenizer, "baz", Tokens.identifier("baz"));
  }
  
  public void testLexicon_caseInsensitive() {
    String[] keywords = {"foo", "Bar"};
    Lexicon lexicon = Keywords.lexicon(Scanners.IDENTIFIER, keywords, false, TokenizerMaps.IDENTIFIER_FRAGMENT);
    for (String keyword : keywords) {
      assertEquals(Tokens.reserved(keyword), lexicon.word(keyword));
      assertEquals(Tokens.reserved(keyword), lexicon.word(keyword.toUpperCase()));
    }
    for (String keyword : keywords) {
      assertParser(lexicon.tokenizer, keyword, Tokens.reserved(keyword));
      assertParser(
          lexicon.tokenizer, keyword.toUpperCase(), Tokens.reserved(keyword));
    }
    assertParser(lexicon.tokenizer, "baz", Tokens.identifier("baz"));
  }
  
  public void testUnique() {
    Asserts.assertArrayEquals(
        Keywords.unique(String.CASE_INSENSITIVE_ORDER, "foo", "Foo", "foo", "bar"),
        "bar", "foo");
  }
}
