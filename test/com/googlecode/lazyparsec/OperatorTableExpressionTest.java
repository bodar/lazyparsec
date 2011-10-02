package com.googlecode.lazyparsec;

import com.googlecode.lazyparsec.easymock.BaseMockTests;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;

import static com.googlecode.lazyparsec.Asserts.assertParser;
import static org.easymock.EasyMock.expect;

/**
 * Unit test for {@link OperatorTable} for building expression parsers.
 *
 * @author Ben Yu
 */
public class OperatorTableExpressionTest extends BaseMockTests {

    // Tests against a sample operator precedence grammar, with {@code +, -, *} as infix
    // left-associative operators, {@code ^} as right-associative operator, {@code ~} as prefix
    // operator, {@code %} as postfix operator, and {@code .} as infix non-associative operator.

    @Mock
    Callable1<String, String> negate;
    @Mock
    Callable2<String, String, String> plus;
    @Mock
    Callable2<String, String, String> subtract;
    @Mock
    Callable2<String, String, String> multiply;
    @Mock
    Callable1<String, String> percent;
    @Mock
    Callable2<String, String, String> point;
    @Mock
    Callable2<String, String, String> power;

    public void testBuildExpressionParser() throws Exception {
        String source = "1+2.3-30%-1+~5*20000%%^2^1*~~3";
        expect(point.call("2", "3")).andReturn("2.3");
        expect(plus.call("1", "2.3")).andReturn("3.3");
        expect(percent.call("30")).andReturn("0.3");
        expect(subtract.call("3.3", "0.3")).andReturn("3.0");
        expect(subtract.call("3.0", "1")).andReturn("2.0");
        expect(negate.call("5")).andReturn("-5");
        expect(percent.call("20000")).andReturn("200");
        expect(percent.call("200")).andReturn("2");
        expect(negate.call("3")).andReturn("-3");
        expect(negate.call("-3")).andReturn("3");
        expect(power.call("2", "1")).andReturn("2");
        expect(power.call("2", "2")).andReturn("4");
        expect(multiply.call("-5", "4")).andReturn("-20");
        expect(multiply.call("-20", "3")).andReturn("-60");
        expect(plus.call("2.0", "-60")).andReturn("-58.0");
        replay();
        assertParser(parser(), source, "-58.0");
    }

    public void testEmptyOperatorTable() {
        Parser<String> operand = Parsers.constant("foo");
        assertSame(operand, new OperatorTable<String>().build(operand));
    }

    private Parser<String> parser() {
        return new OperatorTable<String>()
                .prefix(op("~", negate), 100)
                .postfix(op("%", percent), 80)
                .infixr(op("^", power), 40)
                .infixl(op("+", plus), 10)
                .infixl(op("-", subtract), 10)
                .infixl(op("*", multiply), 20)
                .infixn(op(".", point), 200)
                .build(Scanners.INTEGER.source());
    }

    private <T> Parser<T> op(String name, T value) {
        return Scanners.string(name).retn(value);
    }
}
