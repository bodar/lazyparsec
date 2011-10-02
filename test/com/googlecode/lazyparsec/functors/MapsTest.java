package com.googlecode.lazyparsec.functors;

import java.util.HashMap;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import junit.framework.TestCase;

/**
 * Unit test for {@link Maps}.
 * 
 * @author Ben Yu
 */
public class MapsTest extends TestCase {
  
  public void testToInteger() throws Exception {
    assertEquals(new Integer(123), Maps.TO_INTEGER.call("123"));
    assertEquals("integer", Maps.TO_INTEGER.toString());
  }
  
  public void testToLowerCase() throws Exception {
    assertEquals("foo", Maps.TO_LOWER_CASE.call("Foo"));
    assertEquals("toLowerCase", Maps.TO_LOWER_CASE.toString());
  }
  
  public void testToUpperCase() throws Exception {
    assertEquals("FOO", Maps.TO_UPPER_CASE.call("Foo"));
    assertEquals("toUpperCase", Maps.TO_UPPER_CASE.toString());
  }
  
  private enum MyEnum {
    FOO, BAR
  }
  
  public void testToEnum() throws Exception {
    assertEquals(MyEnum.FOO, Maps.toEnum(MyEnum.class).call("FOO"));
    assertEquals("-> " + MyEnum.class.getName(), Maps.toEnum(MyEnum.class).toString());
  }
  
  public void testIdentity() throws Exception {
    String string = "test";
    assertSame(string, Maps.identity().call(string));
    assertEquals("identity", Maps.identity().toString());
  }
  
  public void testConstant() throws Exception {
    String string = "test";
    assertSame(string, Maps.constant(string).call(1));
    assertEquals("test", Maps.constant(string).toString());
  }
  
  public void testJmap() throws Exception {
    HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
    hashMap.put("one", 1);
    Callable1<String, Integer> callable1 = Maps.map(hashMap);
    assertEquals(hashMap.toString(), callable1.toString());
    assertEquals(1, callable1.call("one").intValue());
    assertNull(callable1.call("two"));
  }
  
  public void testMapToString() throws Exception {
    assertEquals("1", Maps.mapToString().call(1));
    assertEquals("toString", Maps.mapToString().toString());
    assertEquals(String.valueOf((Object) null), Maps.mapToString().call(null));
  }
  
  public void testToPair() throws Exception {
      assertEquals(Pair.pair("one", 1), Maps.toPair().call("one", 1));
    assertEquals("pair", Maps.toPair().toString());
  }
  
  public void testToTuple3() {
    assertEquals(Tuples.tuple("12", 1, 2), Maps.toTriple().map("12", 1, 2));
    assertEquals("tuple", Maps.toTriple().toString());
  }
  
  public void testToTuple4() {
    assertEquals(Tuples.tuple("123", 1, 2, 3), Maps.toQuadruple().map("123", 1, 2, 3));
    assertEquals("tuple", Maps.toQuadruple().toString());
  }
  
  public void testToTuple5() {
    assertEquals(Tuples.tuple("1234", 1, 2, 3, 4), Maps.toQuintuple().map("1234", 1, 2, 3, 4));
    assertEquals("tuple", Maps.toQuintuple().toString());
  }
}
