package com.googlecode.lazyparsec;

import com.googlecode.lazyparsec.OperatorTable.Associativity;
import com.googlecode.lazyparsec.OperatorTable.Operator;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import junit.framework.TestCase;

import static com.googlecode.lazyparsec.OperatorTable.Associativity.*;

/**
 * Unit test for {@link OperatorTable}.
 *
 * @author Ben Yu
 */
public class OperatorTableTest extends TestCase {
    private static final Parser<?> OP = Parsers.never();
    private static final Parser<Callable1<Integer, Integer>> UnaryFunction_OP = Parsers.never();
    private static final Parser<Callable2<Integer, Integer, Integer>> BinaryFunction_OP = Parsers.never();

    public void testAssociativityOrder() {
        assertTotalOrder(PREFIX, POSTFIX, LASSOC, NASSOC, RASSOC);
    }

    public void testOperatorOrder() {
        assertTotalOrder(
                operator(2, PREFIX), operator(2, POSTFIX),
                operator(2, LASSOC), operator(2, NASSOC), operator(2, RASSOC),
                operator(1, PREFIX), operator(1, POSTFIX),
                operator(1, LASSOC), operator(1, NASSOC), operator(1, RASSOC));
    }

    public void testGetOperators() {
        OperatorTable<Integer> table = new OperatorTable<Integer>()
                .infixl(BinaryFunction_OP, 2)
                .infixr(BinaryFunction_OP, 1)
                .prefix(UnaryFunction_OP, 4)
                .postfix(UnaryFunction_OP, 3)
                .postfix(UnaryFunction_OP, 3)
                .infixn(BinaryFunction_OP, 5);
        assertNotNull(table);
        Operator[] operators = table.operators();
        assertEquals(6, operators.length);
        assertEquals(5, operators[0].precedence);
        assertEquals(4, operators[1].precedence);
        assertEquals(3, operators[2].precedence);
        assertEquals(3, operators[3].precedence);
        assertEquals(2, operators[4].precedence);
        assertEquals(1, operators[5].precedence);
    }

    private static <T extends Comparable<T>> void assertTotalOrder(T... objects) {
        for (int i = 0; i < objects.length; i++) {
            assertSameOrder(objects[i]);
            for (int j = i + 1; j < objects.length; j++) {
                assertOrder(objects[i], objects[j]);
            }
        }
    }

    private static <T extends Comparable<T>> void assertOrder(T obj1, T obj2) {
        assertTrue(obj1 + " should be before " + obj2, obj1.compareTo(obj2) < 0);
        assertTrue(obj2 + " should be after " + obj1, obj2.compareTo(obj1) > 0);
    }

    private static <T extends Comparable<T>> void assertSameOrder(T obj) {
        assertEquals(obj + " should be equal to itself", 0, obj.compareTo(obj));
    }

    private static Operator operator(int precedence, Associativity associativity) {
        return new Operator(OP, precedence, associativity);
    }
}
