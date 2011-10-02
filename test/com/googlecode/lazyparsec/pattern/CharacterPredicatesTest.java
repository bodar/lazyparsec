package com.googlecode.lazyparsec.pattern;

import com.googlecode.totallylazy.Predicate;
import junit.framework.TestCase;

import static com.googlecode.lazyparsec.pattern.CharacterPredicates.*;

/**
 * Unit test for {@link CharacterPredicates}.
 *
 * @author Ben Yu
 */
public class CharacterPredicatesTest extends TestCase {

    public void testIsChar() {
        Predicate<Character> predicate = CharacterPredicates.isChar('a');
        assertTrue(predicate.matches('a'));
        assertFalse(predicate.matches('x'));
        assertEquals("a", predicate.toString());
    }

    public void testNotChar() {
        Predicate<Character> predicate = CharacterPredicates.notChar('a');
        assertFalse(predicate.matches('a'));
        assertTrue(predicate.matches('x'));
        assertEquals("^a", predicate.toString());
    }

    public void testRange() {
        Predicate<Character> predicate = CharacterPredicates.range('1', '3');
        assertTrue(predicate.matches('1'));
        assertTrue(predicate.matches('2'));
        assertTrue(predicate.matches('3'));
        assertFalse(predicate.matches('0'));
        assertFalse(predicate.matches('4'));
        assertEquals("[1-3]", predicate.toString());
    }

    public void testIsDigit() {
        Predicate<Character> predicate = CharacterPredicates.IS_DIGIT;
        assertTrue(predicate.matches('0'));
        assertTrue(predicate.matches('9'));
        assertFalse(predicate.matches('a'));
        assertFalse(predicate.matches(' '));
        assertEquals("[0-9]", predicate.toString());
    }

    public void testNotRange() {
        Predicate<Character> predicate = CharacterPredicates.notRange('1', '3');
        assertFalse(predicate.matches('1'));
        assertFalse(predicate.matches('2'));
        assertFalse(predicate.matches('3'));
        assertTrue(predicate.matches('0'));
        assertTrue(predicate.matches('4'));
        assertEquals("[^1-3]", predicate.toString());
    }

    public void testAmong() {
        Predicate<Character> predicate = CharacterPredicates.among("a1");
        assertTrue(predicate.matches('a'));
        assertTrue(predicate.matches('1'));
        assertFalse(predicate.matches(' '));
        assertEquals("[a1]", predicate.toString());
    }

    public void testNotAmong() {
        Predicate<Character> predicate = CharacterPredicates.notAmong("a1");
        assertFalse(predicate.matches('a'));
        assertFalse(predicate.matches('1'));
        assertTrue(predicate.matches(' '));
        assertEquals("^[a1]", predicate.toString());
    }

    public void testIsHexDigit() {
        Predicate<Character> predicate = CharacterPredicates.IS_HEX_DIGIT;
        assertFalse(predicate.matches('g'));
        assertFalse(predicate.matches(' '));
        assertTrue(predicate.matches('A'));
        assertTrue(predicate.matches('a'));
        assertTrue(predicate.matches('F'));
        assertTrue(predicate.matches('f'));
        assertTrue(predicate.matches('0'));
        assertTrue(predicate.matches('9'));
        assertTrue(predicate.matches('E'));
        assertTrue(predicate.matches('1'));
        assertEquals("[0-9a-fA-F]", predicate.toString());
    }

    public void testIsUpperCase() {
        Predicate<Character> predicate = CharacterPredicates.IS_UPPER_CASE;
        assertFalse(predicate.matches('a'));
        assertFalse(predicate.matches('1'));
        assertFalse(predicate.matches(' '));
        assertTrue(predicate.matches('A'));
        assertTrue(predicate.matches('Z'));
        assertEquals("uppercase", predicate.toString());
    }

    public void testIsLowerCase() {
        Predicate<Character> predicate = CharacterPredicates.IS_LOWER_CASE;
        assertFalse(predicate.matches('A'));
        assertFalse(predicate.matches('1'));
        assertFalse(predicate.matches(' '));
        assertTrue(predicate.matches('a'));
        assertTrue(predicate.matches('z'));
        assertEquals("lowercase", predicate.toString());
    }

    public void testIsWhitespace() {
        Predicate<Character> predicate = CharacterPredicates.IS_WHITESPACE;
        assertFalse(predicate.matches('A'));
        assertFalse(predicate.matches('1'));
        assertFalse(predicate.matches('a'));
        assertTrue(predicate.matches(' '));
        assertTrue(predicate.matches('\t'));
        assertTrue(predicate.matches('\n'));
        assertEquals("whitespace", predicate.toString());
    }

    public void testIsAlpha() {
        Predicate<Character> predicate = CharacterPredicates.IS_ALPHA;
        assertFalse(predicate.matches('-'));
        assertFalse(predicate.matches('1'));
        assertFalse(predicate.matches('_'));
        assertTrue(predicate.matches('a'));
        assertTrue(predicate.matches('Z'));
        assertEquals("[a-zA-Z]", predicate.toString());
    }

    public void testIsAlpha_() {
        Predicate<Character> predicate = CharacterPredicates.IS_ALPHA_;
        assertFalse(predicate.matches('-'));
        assertFalse(predicate.matches('1'));
        assertTrue(predicate.matches('_'));
        assertTrue(predicate.matches('a'));
        assertTrue(predicate.matches('Z'));
        assertEquals("[a-zA-Z_]", predicate.toString());
    }

    public void testIsAlphaNumeric() {
        Predicate<Character> predicate = CharacterPredicates.IS_ALPHA_NUMERIC;
        assertFalse(predicate.matches('-'));
        assertFalse(predicate.matches('_'));
        assertTrue(predicate.matches('1'));
        assertTrue(predicate.matches('a'));
        assertTrue(predicate.matches('Z'));
        assertEquals("[0-9a-zA-Z]", predicate.toString());
    }

    public void testIsAlphaNumeric_() {
        Predicate<Character> predicate = CharacterPredicates.IS_ALPHA_NUMERIC_;
        assertFalse(predicate.matches('-'));
        assertTrue(predicate.matches('1'));
        assertTrue(predicate.matches('_'));
        assertTrue(predicate.matches('a'));
        assertTrue(predicate.matches('Z'));
        assertEquals("[0-9a-zA-Z_]", predicate.toString());
    }

    public void testIsLetter() {
        Predicate<Character> predicate = CharacterPredicates.IS_LETTER;
        assertFalse(predicate.matches('-'));
        assertFalse(predicate.matches('1'));
        assertFalse(predicate.matches('_'));
        assertTrue(predicate.matches('a'));
        assertTrue(predicate.matches('Z'));
        assertEquals("letter", predicate.toString());
    }

    public void testAlways() {
        assertTrue(ALWAYS.matches('a'));
        assertTrue(ALWAYS.matches('>'));
        assertTrue(ALWAYS.matches('0'));
        assertEquals("any character", ALWAYS.toString());
    }

    public void testNever() {
        assertFalse(NEVER.matches('a'));
        assertFalse(NEVER.matches('>'));
        assertFalse(NEVER.matches('0'));
        assertEquals("none", NEVER.toString());
    }

    public void testNot() {
        assertFalse(not(ALWAYS).matches('a'));
        assertTrue(not(NEVER).matches('a'));
        assertEquals("^any character", not(ALWAYS).toString());
    }

    public void testAnd() {
        assertSame(ALWAYS, and());
        assertSame(CharacterPredicates.IS_ALPHA, and(CharacterPredicates.IS_ALPHA));
        assertFalse(and(ALWAYS, NEVER).matches('a'));
        assertFalse(and(NEVER, ALWAYS).matches('a'));
        assertFalse(and(NEVER, NEVER).matches('a'));
        assertTrue(and(ALWAYS, ALWAYS).matches('a'));
        assertFalse(and(ALWAYS, NEVER, ALWAYS).matches('a'));
        assertFalse(and(NEVER, ALWAYS, ALWAYS).matches('a'));
        assertFalse(and(NEVER, NEVER, NEVER).matches('a'));
        assertTrue(and(ALWAYS, ALWAYS, ALWAYS).matches('a'));
        assertEquals("any character and none", and(ALWAYS, NEVER).toString());
        assertEquals("any character and none and any character", and(ALWAYS, NEVER, ALWAYS).toString());
    }

    public void testOr() {
        assertSame(NEVER, or());
        assertSame(CharacterPredicates.IS_ALPHA, or(CharacterPredicates.IS_ALPHA));
        assertTrue(or(ALWAYS, NEVER).matches('a'));
        assertTrue(or(NEVER, ALWAYS).matches('a'));
        assertTrue(or(ALWAYS, ALWAYS).matches('a'));
        assertFalse(or(NEVER, NEVER).matches('a'));
        assertTrue(or(ALWAYS, NEVER, NEVER).matches('a'));
        assertTrue(or(NEVER, NEVER, ALWAYS).matches('a'));
        assertTrue(or(ALWAYS, ALWAYS, ALWAYS).matches('a'));
        assertFalse(or(NEVER, NEVER, NEVER).matches('a'));
        assertEquals("any character or none", or(ALWAYS, NEVER).toString());
        assertEquals("any character or none or any character", or(ALWAYS, NEVER, ALWAYS).toString());
    }
}
