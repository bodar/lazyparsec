package com.googlecode.lazyparsec.misc;

import com.googlecode.lazyparsec.Parser;
import com.googlecode.lazyparsec.Parsers;
import com.googlecode.lazyparsec.functors.Binary;
import com.googlecode.lazyparsec.functors.Unary;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.googlecode.lazyparsec.Parsers.constant;
import static com.googlecode.lazyparsec.misc.Mapper._;

/**
 * Unit test for {@link Mapper}.
 *
 * @author Ben Yu
 */
public class MapperTest extends TestCase {

    static final class Foo {
        final String name;

        public Foo(String name) {
            this.name = name;
        }
    }

    private static class CharSequenceMap extends Mapper<CharSequence> {
        @SuppressWarnings("unused")
        String map(String s, Integer n) {
            return s + n;
        }
    }

    private static final Mapper<CharSequence> MAPPER = new CharSequenceMap();

    public void testName() {
        assertEquals(String.class.getName(), MAPPER.name());
    }

    public void testSequence() {
        assertEquals("foo1",
                MAPPER.sequence(Parsers.constant("foo"), Parsers.constant(1)).parse(""));
    }

    public void testCurry_sequence() {
        Parser<Foo> parser =
                Mapper.curry(Foo.class).sequence(Parsers.constant("foo"));
        Foo foo = parser.parse("");
        assertEquals("foo", foo.name);
    }

    public void testAsMap() throws Exception {
        assertEquals(String.class.getName(), MAPPER.asMap().toString());
        assertEquals("s1", MAPPER.asMap().call(new Object[]{"s", 1}));
        assertEquals("null1", MAPPER.asMap().call(new Object[]{null, 1}));
        assertEquals("snull", MAPPER.asMap().call(new Object[]{"s", null}));
    }

    public void testUnary() throws Exception {
        Parser<Unary<Object>> parser = new Mapper<Object>() {
            @SuppressWarnings("unused")
            int map(String text) {
                return Integer.parseInt(text);
            }
        }.unary();
        assertEquals("int", parser.toString());
        Unary<Object> unary = parser.parse("");
        assertEquals(10, unary.call("10"));
    }

    public void testBinary() throws Exception {
        Parser<Binary<Object>> parser = new Mapper<Object>() {
            @SuppressWarnings("unused")
            String map(String name, int i) {
                return name + i;
            }
        }.binary();
        assertEquals(String.class.getName(), parser.toString());
        Binary<Object> binary = parser.parse("");
        assertEquals("a1", binary.call("a", 1));
    }

    public void testPrefix() throws Exception {
        Unary<Object> unary = new Mapper<Object>() {
            @SuppressWarnings("unused")
            String map(String name, int i) {
                return name + i;
            }
        }.prefix(constant("a")).parse("");
        assertEquals("a1", unary.call(1));
    }

    public void testPrefix_multiOp() throws Exception {
        Unary<Object> unary = new Mapper<Object>() {
            @SuppressWarnings("unused")
            String map(String name, int i, int j) {
                return name + i + j;
            }
        }.prefix(constant("a"), constant(1)).parse("");
        assertEquals("a12", unary.call(2));
    }

    public void testPostfix() throws Exception {
        Unary<Object> unary = new Mapper<Object>() {
            @SuppressWarnings("unused")
            String map(String name, int i) {
                return name + i;
            }
        }.postfix(constant(2)).parse("");
        assertEquals("a2", unary.call("a"));
    }

    public void testPostfix_multiOp() throws Exception {
        Unary<Object> unary = new Mapper<Object>() {
            @SuppressWarnings("unused")
            String map(String name, int i, int j) {
                return name + i + j;
            }
        }.postfix(constant(1), constant(2)).parse("");
        assertEquals("a12", unary.call("a"));
    }

    public void testInfix() throws Exception {
        Binary<Object> unary = new Mapper<Object>() {
            @SuppressWarnings("unused")
            String map(String name, int i, int j) {
                return name + i + j;
            }
        }.infix(constant(1)).parse("");
        assertEquals("a12", unary.call("a", 2));
    }

    public void testInfix_multiOp() throws Exception {
        Binary<Object> unary = new Mapper<Object>() {
            @SuppressWarnings("unused")
            String map(String name, int i, int j, int k) {
                return name + i + j + k;
            }
        }.infix(constant(1), constant(2)).parse("");
        assertEquals("a123", unary.call("a", 3));
    }

    public void testMap_errorPropagated() {
        final Error error = new Error();
        Mapper<String> collector = new Mapper<String>() {
            @SuppressWarnings("unused")
            String map(String s, Integer i) {
                throw error;
            }
        };
        try {
            collector.asMap().call(new Object[]{"s", 1});
            fail();
        } catch (Error e) {
            assertSame(error, e);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public void testMap_runtimeExceptionPropagated() throws Exception {
        final RuntimeException exception = new RuntimeException();
        Mapper<String> collector = new Mapper<String>() {
            @SuppressWarnings("unused")
            String map(String s, Integer i) {
                throw exception;
            }
        };
        try {
            collector.asMap().call(new Object[]{"s", 1});
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e);
        }
    }

    public void testMap_exceptionPropagated() throws Exception {
        final Exception exception = new Exception();
        Mapper<String> collector = new Mapper<String>() {
            @SuppressWarnings("unused")
            String map(String s, Integer i) throws Exception {
                throw exception;
            }
        };
        try {
            collector.asMap().call(new Object[]{"s", 1});
            fail();
        } catch (RuntimeException e) {
            assertSame(exception, e.getCause());
        }
    }

    public void testWrongParameters() throws Exception {
        try {
            MAPPER.asMap().call(new Object[]{1, 2, 3});
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(),
                    e.getMessage().contains("3 arguments received, 2 expected"));
        }
    }

    public void testWrongParametersForSequencing() {
        assertWrongParameters(Mapper.curry(Foo.class), 1, 0);
        assertWrongParameters(Mapper.curry(Foo.class, "foo"), 0, 1);
        assertWrongParameters(fooMapper(), 2, 3);
    }

    private <T> Mapper<T> fooMapper() {
        return new Mapper<T>() {
            @SuppressWarnings("unused")
            Foo map(String name, int unused) {
                return new Foo(name);
            }
        };
    }

    public void testTargetTypeUnknownAtConstructionTime() {
        assertFoo("foo", fooMapper().sequence(constant("foo"), constant(1)).parse(""));
    }

    @SuppressWarnings("unchecked")
    public void testNonGenericMapper() {
        assertEquals("1", new Mapper() {
            @SuppressWarnings("unused")
            public String map(int i) {
                return Integer.toString(i);
            }
        }.sequence(constant(1)).parse(""));
    }

    private static class Thing {
        final String s;
        final int i;
        final long l;
        final boolean b;
        final char c;

        public Thing(String s, int i, long l, boolean b, char c) {
            this.s = s;
            this.i = i;
            this.l = l;
            this.b = b;
            this.c = c;
        }

        @Override
        public String toString() {
            return s + i + l + b + c;
        }
    }

    private static Mapper<Object> thingMapper() {
        return new Mapper<Object>() {
            @SuppressWarnings("unused")
            public Thing map(int i, boolean b, char c) {
                return new Thing("thing", i, 2L, b, c);
            }
        };
    }

    public void testParametersSkippedForSequence() {
        assertEquals("foo12truec",
                Mapper.curry(Thing.class, "foo", 1, 2L).sequence(
                        _(constant("bar")), constant(true), constant('c')).parse("").toString());
        assertEquals("foo12truec",
                Mapper.curry(Thing.class, "foo", 1, 2L).sequence(
                        _(constant(false)), constant(true),
                        _(constant("bar")), _(constant(false)), constant('c'),
                        _(constant('d')), _(constant('e'))).parse("").toString());
        assertFoo("foo",
                fooMapper().sequence(_(constant("bar")), constant("foo"), constant(1)).parse(""));
    }

    public void testParametersSkippedForPrefix() throws Exception {
        assertEquals("foo12truec",
                Mapper.<Object>curry(Thing.class, "foo", 2L).prefix(
                        constant(1), _(constant("bar")), constant(true)).parse("").call('c').toString());
        assertEquals("thing12truec",
                thingMapper().prefix(
                        constant(1), _(constant("bar")), constant(true)).parse("").call('c').toString());
    }

    public void testParametersSkippedForPostfix() throws Exception {
        assertEquals("foo12truec",
                Mapper.<Object>curry(Thing.class, "foo", 2L).postfix(
                        _(constant("bar")), constant(true), constant('c')).parse("").call(1).toString());
        assertEquals("thing12truec",
                thingMapper().postfix(
                        _(constant("bar")), constant(true), constant('c')).parse("").call(1).toString());
    }

    public void testParametersSkippedForInfix() throws Exception {
        assertEquals("foo12truec",
                Mapper.<Object>curry(Thing.class, "foo", 2L).infix(
                        constant(true), _(constant("bar"))).parse("").call(1, 'c').toString());
        assertEquals("thing12truec",
                thingMapper().infix(
                        constant(true), _(constant("bar"))).parse("").call(1, 'c').toString());
    }

    public void testInvalidSkipForPrefix() {
        try {
            Mapper.<Object>curry(Foo.class).prefix(_(constant("bar")));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot skip the only parser parameter.", e.getMessage());
        }
        try {
            Mapper.<Object>curry(Foo.class).prefix(_(constant(1)), _(constant("bar")));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot skip all parser parameters.", e.getMessage());
        }
        try {
            fooMapper().prefix(_(constant(1)));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot skip the only parser parameter.", e.getMessage());
        }
        try {
            fooMapper().prefix(_(constant(1)), _(constant("bar")));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot skip all parser parameters.", e.getMessage());
        }
    }

    public void testInvalidSkipForPostfix() {
        try {
            Mapper.<Object>curry(Foo.class).postfix(_(constant("bar")));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot skip the only parser parameter.", e.getMessage());
        }
        try {
            Mapper.<Object>curry(Foo.class).postfix(_(constant(1)), _(constant("bar")));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot skip all parser parameters.", e.getMessage());
        }
        try {
            fooMapper().postfix(_(constant(1)));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot skip the only parser parameter.", e.getMessage());
        }
        try {
            fooMapper().postfix(_(constant(1)), _(constant("bar")));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot skip all parser parameters.", e.getMessage());
        }
    }

    public void testInvalidSkipForInfix() {
        try {
            Mapper.<Object>curry(Foo.class).infix(_(constant("bar")));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot skip the only parser parameter.", e.getMessage());
        }
        try {
            Mapper.<Object>curry(Foo.class).infix(_(constant(1)), _(constant("bar")));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot skip all parser parameters.", e.getMessage());
        }
        try {
            fooMapper().infix(_(constant(1)));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot skip the only parser parameter.", e.getMessage());
        }
        try {
            fooMapper().infix(_(constant(1)), _(constant("bar")));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Cannot skip all parser parameters.", e.getMessage());
        }
    }

    private void assertFoo(String name, Object actual) {
        assertTrue(actual instanceof Foo);
        assertEquals(name, ((Foo) actual).name);
    }

    private void assertWrongParameters(Mapper<?> mapper, int expected, int provided) {
        Parser<?>[] parsers = new Parser<?>[provided];
        for (int i = 0; i < provided; i++) {
            parsers[i] = Parsers.always();
        }
        try {
            mapper.sequence(parsers);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(),
                    e.getMessage().contains(expected + " parameters expected"));
            assertTrue(e.getMessage(),
                    e.getMessage().contains(provided + " provided."));
        }
    }

    public void testWrongType() throws Exception {
        try {
            MAPPER.asMap().call(new Object[]{1, 1});
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage(),
                    e.getMessage().contains(String.class.getName() + " expected for parameter 0"));
            assertTrue(e.getMessage(),
                    e.getMessage().contains(Integer.class.getName() + " provided"));
        }
    }

    public void testMissingMapperMethod() {
        try {
            new Mapper<String>() {
            };
            fail();
        } catch (IllegalStateException e) {
        }
    }

    public void testAmbiguousMapperMethods() {
        try {
            new Mapper<String>() {
                @SuppressWarnings("unused")
                String map(int i) {
                    return null;
                }

                @SuppressWarnings("unused")
                String map() {
                    return null;
                }
            };
            fail();
        } catch (IllegalStateException e) {
        }
    }

    public void testIncompatibleReturnType() {
        try {
            new Mapper<String>() {
                @SuppressWarnings("unused")
                int map() {
                    return 0;
                }
            };
            fail();
        } catch (IllegalStateException e) {
        }
    }

    public void testIncompatibleGenericReturnType() {
        try {
            new Mapper<List<String>>() {
                @SuppressWarnings("unused")
                Collection<String> map() {
                    return Arrays.asList("foo");
                }
            };
            fail();
        } catch (IllegalStateException e) {
        }
    }

    static class CharSequenceSubMap extends CharSequenceMap {
        @SuppressWarnings("unused")
        @Override
        String map(String s, Integer n) {
            return "sub." + s + n;
        }
    }

    public void testMapperMethodInSuperclass() {
        assertEquals("s1", new CharSequenceMap() {
        }.map("s", 1));
    }

    public void testMapperMethodInSubclass() {
        assertEquals("sub.s1", new CharSequenceSubMap().map("s", 1));
    }
}
