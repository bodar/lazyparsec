package com.googlecode.lazyparsec.misc;

import com.googlecode.lazyparsec.Parser;
import com.googlecode.lazyparsec.Parsers;
import com.googlecode.lazyparsec.error.ParserException;
import com.googlecode.totallylazy.BinaryFunction;
import com.googlecode.totallylazy.UnaryFunction;
import com.googlecode.lazyparsec.util.ObjectTester;
import junit.framework.TestCase;

import static com.googlecode.lazyparsec.Parsers.constant;
import static com.googlecode.lazyparsec.Scanners.string;
import static com.googlecode.lazyparsec.misc.Mapper._;

/**
 * Unit test for {@link Curry}.
 *
 * @author Ben Yu
 */
public class CurryTest extends TestCase {

    static final class Foo {
        final String name;
        final int size;

        public Foo(String name, int size) {
            this.name = name;
            this.size = size;
        }
    }

    static final class Bar {
        final String name;
        final int size;

        public Bar(String name, int size) {
            this.name = name;
            this.size = size;
        }
    }

    static final class Baz {
        final byte b;
        final short s;
        final int i;
        final long l;
        final char c;

        public Baz(byte b, short s, int i, long l, char c) {
            this.b = b;
            this.s = s;
            this.i = i;
            this.l = l;
            this.c = c;
        }
    }

    public void testSequence() {
        Parser<Foo> parser =
                Curry.of(Foo.class).sequence(Parsers.constant("foo"), Parsers.constant(1));
        Foo foo = parser.parse("");
        assertEquals("foo", foo.name);
        assertEquals(1, foo.size);
    }

    public void testUnaryFunction() throws Exception {
        UnaryFunction<Object> UnaryFunction = Curry.<Object>of(Foo.class, 1).UnaryFunction().parse("");
        Foo foo = (Foo) UnaryFunction.call("foo");
        assertEquals("foo", foo.name);
        assertEquals(1, foo.size);
    }

    public void testBinaryFunction() throws Exception {
        BinaryFunction<Object> BinaryFunction = Curry.<Object>of(Foo.class).BinaryFunction().parse("");
        Foo foo = (Foo) BinaryFunction.call("foo", 2);
        assertEquals("foo", foo.name);
        assertEquals(2, foo.size);
    }

    interface Expr {
    }

    private static final Expr FAKE_EXPR = new Expr() {
    };

    static class PrefixExpr implements Expr {
        final String op;
        final Expr expr;

        public PrefixExpr(String op, Expr expr) {
            this.op = op;
            this.expr = expr;
        }
    }

    static class PrefixExpr2 implements Expr {
        final String op;
        final int size;
        final Expr expr;

        public PrefixExpr2(String op, int size, Expr expr) {
            this.op = op;
            this.size = size;
            this.expr = expr;
        }
    }

    static class PostfixExpr implements Expr {
        final Expr expr;
        final String op;

        public PostfixExpr(Expr expr, String op) {
            this.expr = expr;
            this.op = op;
        }
    }

    static class PostfixExpr2 implements Expr {
        final Expr expr;
        final int size;
        final String op;

        public PostfixExpr2(Expr expr, int size, String op) {
            this.expr = expr;
            this.size = size;
            this.op = op;
        }
    }

    static class InfixExpr implements Expr {
        final Expr left;
        final String op;
        final Expr right;

        public InfixExpr(Expr left, String op, Expr right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }
    }

    static class InfixExpr2 implements Expr {
        final Expr left;
        final String op;
        final int size;
        final Expr right;

        public InfixExpr2(Expr left, String op, int size, Expr right) {
            this.left = left;
            this.op = op;
            this.size = size;
            this.right = right;
        }
    }

    public void testPrefix() throws Exception {
        Expr result = Curry.<Expr>of(PrefixExpr.class).prefix(constant("x"))
                .parse("").call(FAKE_EXPR);
        PrefixExpr prefix = (PrefixExpr) result;
        assertEquals("x", prefix.op);
        assertSame(FAKE_EXPR, prefix.expr);
    }

    public void testPrefix_onlyOneUnskippedOperator() throws Exception {
        Expr result = Curry.<Expr>of(PrefixExpr.class).prefix(_(string("foo")), constant("x"))
                .parse("foo").call(FAKE_EXPR);
        PrefixExpr prefix = (PrefixExpr) result;
        assertEquals("x", prefix.op);
        assertSame(FAKE_EXPR, prefix.expr);
    }

    public void testPrefix_multiOp() throws Exception {
        Expr result = Curry.<Expr>of(PrefixExpr2.class).prefix(constant("x"), constant(2))
                .parse("").call(FAKE_EXPR);
        PrefixExpr2 prefix = (PrefixExpr2) result;
        assertEquals("x", prefix.op);
        assertEquals(2, prefix.size);
        assertSame(FAKE_EXPR, prefix.expr);
    }

    public void testPostfix() throws Exception {
        Expr result = Curry.<Expr>of(PostfixExpr.class).postfix(constant("x"))
                .parse("").call(FAKE_EXPR);
        PostfixExpr postfix = (PostfixExpr) result;
        assertEquals("x", postfix.op);
        assertSame(FAKE_EXPR, postfix.expr);
    }

    public void testPostfix_onlyOneUnskippedOperator() throws Exception {
        Expr result = Curry.<Expr>of(PostfixExpr.class).postfix(_(string("foo")), constant("x"))
                .parse("foo").call(FAKE_EXPR);
        PostfixExpr postfix = (PostfixExpr) result;
        assertEquals("x", postfix.op);
        assertSame(FAKE_EXPR, postfix.expr);
    }

    public void testPostfix_multiOp() throws Exception {
        Expr result = Curry.<Expr>of(PostfixExpr2.class).postfix(constant(1), constant("x"))
                .parse("").call(FAKE_EXPR);
        PostfixExpr2 postfix = (PostfixExpr2) result;
        assertEquals("x", postfix.op);
        assertEquals(1, postfix.size);
        assertSame(FAKE_EXPR, postfix.expr);
    }

    public void testInfix() throws Exception {
        Expr left = FAKE_EXPR;
        Expr right = new Expr() {
        };
        Expr result = Curry.<Expr>of(InfixExpr.class).infix(constant("x"))
                .parse("").call(left, right);
        InfixExpr infix = (InfixExpr) result;
        assertEquals("x", infix.op);
        assertSame(left, infix.left);
        assertSame(right, infix.right);
    }

    public void testInfix_onlyOneUnskippedOperator() throws Exception {
        Expr left = FAKE_EXPR;
        Expr right = new Expr() {
        };
        Expr result = Curry.<Expr>of(InfixExpr.class).infix(_(string("foo")), constant("x"))
                .parse("foo").call(left, right);
        InfixExpr infix = (InfixExpr) result;
        assertEquals("x", infix.op);
        assertSame(left, infix.left);
        assertSame(right, infix.right);
    }

    public void testInfix_multiOp() throws Exception {
        Expr left = FAKE_EXPR;
        Expr right = new Expr() {
        };
        Expr result = Curry.<Expr>of(InfixExpr2.class).infix(constant("x"), constant(3))
                .parse("").call(left, right);
        InfixExpr2 infix = (InfixExpr2) result;
        assertEquals("x", infix.op);
        assertEquals(3, infix.size);
        assertSame(left, infix.left);
        assertSame(right, infix.right);
    }

    static final class ThrowError {
        public ThrowError(String message) {
            throw new AssertionError(message);
        }
    }

    public void testSequence_propagatesError() {
        Parser<ThrowError> parser = Curry.of(ThrowError.class).sequence(Parsers.constant("foo"));
        try {
            parser.parse("");
            fail();
        } catch (AssertionError e) {
            assertEquals("foo", e.getMessage());
        }
    }

    static final class ThrowUncheckedException {
        public ThrowUncheckedException(String message) {
            throw new IllegalArgumentException(message);
        }
    }

    public void testSequence_propagatesUncheckedException() {
        Parser<ThrowUncheckedException> parser =
                Curry.of(ThrowUncheckedException.class).sequence(Parsers.constant("foo"));
        try {
            parser.parse("");
            fail();
        } catch (ParserException e) {
            assertEquals("foo", e.getCause().getMessage());
        }
    }

    static final class ThrowCheckedException {
        public ThrowCheckedException(String message) throws Exception {
            throw new Exception(message);
        }
    }

    public void testSequence_propagatesCheckedException() {
        Parser<ThrowCheckedException> parser =
                Curry.of(ThrowCheckedException.class).sequence(Parsers.constant("foo"));
        try {
            parser.parse("");
            fail();
        } catch (ParserException e) {
            assertEquals("foo", e.getCause().getCause().getMessage());
        }
    }

    public void testToString() {
        assertEquals(Foo.class.getName(), Curry.of(Foo.class).toString());
    }

    public void testName() {
        assertEquals(Foo.class.getName(), Curry.of(Foo.class).name());
    }

    public void testEquals() {
        ObjectTester.assertEqual(Curry.of(Foo.class), Curry.of(Foo.class));
        ObjectTester.assertEqual(Curry.of(Foo.class, "foo"), Curry.of(Foo.class, "foo"));
        ObjectTester.assertNotEqual(Curry.of(Foo.class, "foo"),
                Curry.of(Foo.class, "bar"), Curry.of(Bar.class, "foo"));
    }

    static abstract class AbstractBar {
        public AbstractBar() {
        }
    }

    public void testAbstractClass() {
        try {
            Curry.of(AbstractBar.class);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot curry abstract class: " + AbstractBar.class.getName(), e.getMessage());
        }
    }

    static class NoPublicConstructor {
        NoPublicConstructor() {
        }
    }

    public void testNoPublicConstructor() {
        try {
            Curry.of(NoPublicConstructor.class);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(),
                    e.getMessage().contains("public constructor in " + NoPublicConstructor.class.getName()));
        }
    }

    static class AmbiguousConstructor {
        public AmbiguousConstructor(int i) {
        }

        public AmbiguousConstructor(String n) {
        }
    }

    public void testAmbiguousConstructor() {
        try {
            Curry.of(AmbiguousConstructor.class);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(),
                    e.getMessage().contains("public constructor in " + AmbiguousConstructor.class.getName()));
        }
    }

    static class VarargConstructor {
        public VarargConstructor(String... names) {
        }
    }

    public void testVarargConstructor() {
        try {
            Curry.of(VarargConstructor.class);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(),
                    e.getMessage().contains("Cannot curry for constructor with varargs"));
        }
    }

    public void testTooManyCurryArgs() {
        try {
            Curry.of(Foo.class, "foo", 1, 2);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    public void testCurryArgTypeMismatch() {
        try {
            Curry.of(Foo.class, 1L);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Long"));
        }
    }

    public void testAmbiguousCurryArg() {
        try {
            Curry.of(Foo.class, 1, 2);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("int"));
        }
    }

    public void testWrongArgumentType() throws Exception {
        try {
            Curry.<Object>of(Foo.class).asBinaryFunction().call("foo", 2L);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("int"));
        }
    }

    public void testAsUnaryFunction_wrongParamNumber() {
        Curry<Foo> curry = Curry.of(Foo.class);
        try {
            curry.asUnaryFunction();
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    public void testAsBinaryFunction_wrongParamNumber() {
        Curry<Foo> curry = Curry.of(Foo.class, 1);
        try {
            curry.asBinaryFunction();
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    public void testPrefix_wrongParamNumber() {
        Curry<Foo> curry = Curry.of(Foo.class, 1);
        try {
            curry.prefix(Parsers.constant(2));
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("2 parameters expected"));
            assertTrue(e.getMessage(), e.getMessage().contains("3 will be provided"));
        }
    }

    public void testPrefix_multiOp_wrongParamNumber() {
        Curry<Foo> curry = Curry.of(Foo.class);
        try {
            curry.prefix(Parsers.constant(1), Parsers.constant(2));
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("2 parameters expected"));
            assertTrue(e.getMessage(), e.getMessage().contains("3 will be provided"));
        }
    }

    public void testPostfix_wrongParamNumber() {
        Curry<Foo> curry = Curry.of(Foo.class, 1);
        try {
            curry.postfix(Parsers.constant(2));
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("2 parameters expected"));
            assertTrue(e.getMessage(), e.getMessage().contains("3 will be provided"));
        }
    }

    public void testPostfix_multiOp_wrongParamNumber() {
        Curry<Foo> curry = Curry.of(Foo.class);
        try {
            curry.postfix(Parsers.constant(1), Parsers.constant(2));
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("2 parameters expected"));
            assertTrue(e.getMessage(), e.getMessage().contains("3 will be provided"));
        }
    }

    public void testInfix_wrongParamNumber() {
        Curry<Foo> curry = Curry.of(Foo.class);
        try {
            curry.infix(Parsers.constant(2));
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("2 parameters expected"));
            assertTrue(e.getMessage(), e.getMessage().contains("3 will be provided"));
        }
    }

    public void testInfix_multiOp_wrongParamNumber() {
        Curry<Foo> curry = Curry.of(Foo.class);
        try {
            curry.infix(Parsers.constant(1), Parsers.constant(2));
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("2 parameters expected"));
            assertTrue(e.getMessage(), e.getMessage().contains("4 will be provided"));
        }
    }

    public void testInvoke_wrongParameterNumber() throws Throwable {
        try {
            Curry.of(Foo.class, 1).invoke(new Object[]{"foo", "bar"});
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("1 parameters expected, 2 provided: "));
        }
    }
}
