package com.googlecode.lazyparsec;

import com.googlecode.lazyparsec.easymock.BaseMockTests;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Callable3;
import com.googlecode.totallylazy.Callable4;
import com.googlecode.totallylazy.Callable5;
import junit.framework.TestCase;

import static org.easymock.EasyMock.expect;

/**
 * Unit test for {@link InternalFunctors}.
 *
 * @author Ben Yu
 */
public class InternalFunctorsTest extends TestCase {

    public void testIsTokenType() {
        TokenMap<Integer> fromToken = InternalFunctors.isTokenType(Integer.class, "int");
        assertEquals("int", fromToken.toString());
        assertNull(fromToken.map(new Token(1, 1, "foo")));
        assertNull(fromToken.map(new Token(1, 1, null)));
        assertEquals(Integer.valueOf(1), fromToken.map(new Token(1, 1, 1)));
    }

    public void testTokenWithSameValue() {
        Integer i = new Integer(10);
        TokenMap<Token> fromToken = InternalFunctors.tokenWithSameValue(i);
        assertEquals("10", fromToken.toString());
        assertNull(fromToken.map(new Token(1, 1, "foo")));
        assertNull(fromToken.map(new Token(1, 1, 2)));
        assertNull(fromToken.map(new Token(1, 1, null)));
        Token token = new Token(1, 1, i);
        assertSame(token, fromToken.map(token));
    }

    public void testFirstOfTwo() throws Exception {
        Callable2<String, Integer, String> callable = InternalFunctors.firstOfTwo();
        assertEquals("followedBy", callable.toString());
        assertEquals("one", callable.call("one", 2));
    }

    public void testLastOfTwo() throws Exception {
        Callable2<Integer, String, String> callable = InternalFunctors.lastOfTwo();
        assertEquals("sequence", callable.toString());
        assertEquals("two", callable.call(1, "two"));
    }

    public void testLastOfThree() throws Exception {
        Callable3<Integer, String, String, String> callable = InternalFunctors.lastOfThree();
        assertEquals("sequence", callable.toString());
        assertEquals("three", callable.call(1, "two", "three"));
    }

    public void testLastOfFour() throws Exception {
        Callable4<Integer, String, String, String, String> callable = InternalFunctors.lastOfFour();
        assertEquals("sequence", callable.toString());
        assertEquals("four", callable.call(1, "two", "three", "four"));
    }

    public void testLastOfFive() throws Exception {
        Callable5<Integer, String, String, String, String, String> callable = InternalFunctors.lastOfFive();
        assertEquals("sequence", callable.toString());
        assertEquals("five", callable.call(1, "two", "three", "four", "five"));
    }

    public static class FallbackTest extends BaseMockTests {
        @Mock
        Callable1<String, Integer> callable11;
        @Mock
        Callable1<String, Integer> callable12;

        public void testFirstMapReturnsNonNull() throws Exception {
            expect(callable11.call("one")).andReturn(1);
            replay();
            assertEquals(Integer.valueOf(1), fallback().call("one"));
        }

        public void testFirstMapReturnsNull() throws Exception {
            expect(callable11.call("one")).andReturn(null);
            expect(callable12.call("one")).andReturn(1);
            replay();
            assertEquals(Integer.valueOf(1), fallback().call("one"));
        }

        public void testBothMapsReturnNull() throws Exception {
            expect(callable11.call("null")).andReturn(null);
            expect(callable12.call("null")).andReturn(null);
            replay();
            assertNull(fallback().call("null"));
        }

        public void testToString() {
            assertEquals("fallback", fallback().toString());
        }

        private Callable1<String, Integer> fallback() {
            return InternalFunctors.fallback(callable11, callable12);
        }
    }
}
