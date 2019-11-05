package de.comroid.test.vban;

import org.junit.Test;

import static de.comroid.vban.Util.checkRange;

public class UtilsTest {
    @Test
    public void testWithinBounds() {
        checkRange(5, 0, 10);
        checkRange(0, 0, 10);
        checkRange(10, 0, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutOfLowerBounds() {
        checkRange(-1, 0, 255);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutOfUpperBounds() {
        checkRange(256, 0, 255);
    }
}
