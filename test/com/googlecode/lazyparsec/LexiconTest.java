package com.googlecode.lazyparsec;

import com.googlecode.lazyparsec.functors.Maps;
import com.googlecode.totallylazy.Callable1;
import junit.framework.TestCase;

/**
 * Unit test for {@link Lexicon}.
 *
 * @author Ben Yu
 */
public class LexiconTest extends TestCase {

    public void testWord() {
        Callable1<String, Object> callable1 = Maps.<String, Object>constant("foo");
        Parser<?> tokenizer = Terminals.CharLiteral.SINGLE_QUOTE_TOKENIZER;
        Lexicon lexicon = new Lexicon(callable1, tokenizer);
        assertSame(tokenizer, lexicon.tokenizer);
        assertEquals("foo", lexicon.word("whatever"));
    }

    public void testWord_throwsForNullValue() {
        Callable1<String, Object> callable1 = Maps.<String, Object>constant(null);
        Parser<?> tokenizer = Terminals.CharLiteral.SINGLE_QUOTE_TOKENIZER;
        Lexicon lexicon = new Lexicon(callable1, tokenizer);
        assertSame(tokenizer, lexicon.tokenizer);
        try {
            lexicon.word("whatever");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
}
