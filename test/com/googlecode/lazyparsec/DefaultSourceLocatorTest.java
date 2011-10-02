package com.googlecode.lazyparsec;

import com.googlecode.lazyparsec.error.Location;
import com.googlecode.lazyparsec.util.IntList;
import junit.framework.TestCase;

/**
 * Unit test for {@link DefaultSourceLocator}.
 *
 * @author Ben Yu
 */
public class DefaultSourceLocatorTest extends TestCase {
    public void testLocate_onlyOneLineBreakCharacter() {
        DefaultSourceLocator locator = new DefaultSourceLocator("\n");
        Location location = locator.locate(0);
        assertEquals(new Location(1, 1), location);
        assertEquals(location, locator.locate(0));
        assertEquals(new Location(2, 1), locator.locate(1));
    }

    public void testLocate_emptySource() {
        DefaultSourceLocator locator = new DefaultSourceLocator("");
        Location location = locator.locate(0);
        assertEquals(new Location(1, 1), location);
        assertEquals(location, locator.locate(0));
    }

    public void testBinarySearch_firstElementIsEqual() {
        assertEquals(0, DefaultSourceLocator.binarySearch(intList(1, 2, 3), 1));
    }

    public void testBinarySearch_firstElementIsBigger() {
        assertEquals(0, DefaultSourceLocator.binarySearch(intList(1, 2, 3), 0));
    }

    public void testBinarySearch_secondElementIsEqual() {
        assertEquals(1, DefaultSourceLocator.binarySearch(intList(1, 2, 3), 2));
    }

    public void testBinarySearch_secondElementIsBigger() {
        assertEquals(1, DefaultSourceLocator.binarySearch(intList(1, 3, 5), 2));
    }

    public void testBinarySearch_lastElementIsEqual() {
        assertEquals(2, DefaultSourceLocator.binarySearch(intList(1, 3, 5), 5));
    }

    public void testBinarySearch_lastElementIsBigger() {
        assertEquals(2, DefaultSourceLocator.binarySearch(intList(1, 3, 5), 4));
    }

    public void testBinarySearch_allSmaller() {
        assertEquals(3, DefaultSourceLocator.binarySearch(intList(1, 3, 5), 10));
    }

    public void testBinarySearch_oneEqualElement() {
        assertEquals(0, DefaultSourceLocator.binarySearch(intList(1), 1));
    }

    public void testBinarySearch_oneBiggerElement() {
        assertEquals(0, DefaultSourceLocator.binarySearch(intList(2), 1));
    }

    public void testBinarySearch_oneSmallerElement() {
        assertEquals(1, DefaultSourceLocator.binarySearch(intList(0), 1));
    }

    public void testBinarySearch_noElement() {
        assertEquals(0, DefaultSourceLocator.binarySearch(intList(), 1));
    }

    public void testLookup_noLineBreaksScanned() {
        DefaultSourceLocator locator = new DefaultSourceLocator("whatever", 2, 3);
        assertEquals(new Location(2, 4), locator.lookup(1));
    }

    public void testLookup_inFirstLine() {
        DefaultSourceLocator locator = new DefaultSourceLocator("whatever", 2, 3);
        addLineBreaks(locator, 3, 5, 7);
        assertEquals(new Location(2, 4), locator.lookup(1));
    }

    public void testLookup_firstLineBreak() {
        DefaultSourceLocator locator = new DefaultSourceLocator("whatever", 2, 3);
        addLineBreaks(locator, 3, 5, 7);
        assertEquals(new Location(2, 6), locator.lookup(3));
    }

    public void testLookup_firstCharInSecondLine() {
        DefaultSourceLocator locator = new DefaultSourceLocator("whatever", 2, 3);
        addLineBreaks(locator, 3, 5, 7);
        assertEquals(new Location(3, 1), locator.lookup(4));
    }

    public void testLookup_lastCharInSecondLine() {
        DefaultSourceLocator locator = new DefaultSourceLocator("whatever", 2, 3);
        addLineBreaks(locator, 3, 5, 7);
        assertEquals(new Location(3, 2), locator.lookup(5));
    }

    public void testLookup_firstCharInThirdLine() {
        DefaultSourceLocator locator = new DefaultSourceLocator("whatever", 2, 3);
        addLineBreaks(locator, 3, 5, 7);
        assertEquals(new Location(4, 1), locator.lookup(6));
    }

    public void testLookup_lastCharInThirdLine() {
        DefaultSourceLocator locator = new DefaultSourceLocator("whatever", 2, 3);
        addLineBreaks(locator, 3, 5, 7);
        assertEquals(new Location(4, 2), locator.lookup(7));
    }

    public void testLookup_firstCharInLastLine() {
        DefaultSourceLocator locator = new DefaultSourceLocator("whatever", 2, 3);
        addLineBreaks(locator, 3, 5, 7);
        assertEquals(new Location(5, 1), locator.lookup(8));
    }

    public void testLookup_secondCharInLastLine() {
        DefaultSourceLocator locator = new DefaultSourceLocator("whatever", 2, 3);
        addLineBreaks(locator, 3, 5, 7);
        assertEquals(new Location(5, 2), locator.lookup(9));
    }

    public void testScanTo_indexOutOfBounds() {
        DefaultSourceLocator locator = new DefaultSourceLocator("whatever", 2, 3);
        try {
            locator.scanTo(100);
            fail();
        } catch (StringIndexOutOfBoundsException e) {
        }
    }

    public void testScanTo_indexOnEof() {
        DefaultSourceLocator locator = new DefaultSourceLocator("foo", 2, 3);
        assertEquals(new Location(2, 6), locator.scanTo(3));
        assertEquals(3, locator.nextIndex);
        assertEquals(3, locator.nextColumnIndex);
    }

    public void testScanTo_spansLines() {
        DefaultSourceLocator locator = new DefaultSourceLocator("foo\nbar\n", 2, 3);
        assertEquals(new Location(3, 1), locator.scanTo(4));
        assertEquals(5, locator.nextIndex);
        assertEquals(1, locator.nextColumnIndex);
    }

    public void testScanTo_lastCharOfLine() {
        DefaultSourceLocator locator = new DefaultSourceLocator("foo\nbar\n", 2, 3);
        assertEquals(new Location(3, 4), locator.scanTo(7));
        assertEquals(8, locator.nextIndex);
        assertEquals(0, locator.nextColumnIndex);
    }

    public void testLocate() {
        DefaultSourceLocator locator = new DefaultSourceLocator("foo\nbar\n", 2, 3);
        assertEquals(new Location(3, 4), locator.locate(7));
        assertEquals(new Location(2, 5), locator.locate(2)); // this will call lookup()
    }

    private void addLineBreaks(DefaultSourceLocator locator, int... indices) {
        for (int i : indices) {
            locator.lineBreakIndices.add(i);
        }
    }

    private static IntList intList(int... ints) {
        IntList intList = new IntList();
        for (int i : ints) {
            intList.add(i);
        }
        return intList;
    }
}
