package de.comroid.test.vban.model;

import de.comroid.vban.model.UnfinishedByteArray;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UnfinishedByteArrayTest {
    private UnfinishedByteArray array;

    @Before
    public void setUp() {
        array = new UnfinishedByteArray(2);

        array.append((byte) 32, (byte) 41);
    }

    @Test
    public void testAppending() {
        assertEquals(2, array.length());
        assertEquals(2, array.getBufferSize());

        array.append((byte) 24, (byte) 64);

        assertEquals(4, array.length());
        assertEquals(6, array.getBufferSize());

        assertArrayEquals(new byte[]{32, 41, 24, 64}, array.getBytes());
    }

    @Test(expected = NullPointerException.class)
    public void testNullAppending() {
        //noinspection ConfusingArgumentToVarargsMethod
        array.append(null);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testFixedSize() throws Exception {
        UnfinishedByteArray fixed = new UnfinishedByteArray(2, true);

        try {
            fixed.append(new byte[]{72, 19});
        } catch (ArrayIndexOutOfBoundsException any) {
            throw new Exception(any);
        }

        fixed.append((byte) 15);
    }
}
