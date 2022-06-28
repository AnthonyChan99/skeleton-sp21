package IntList;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SquarePrimesTest {
    @Test
    public void testAddConstantOne() {
        IntList lst = IntList.of(2);
        boolean isSquared = IntListExercises.squarePrimes(lst);
        assertEquals("4", lst.toString());
        assertEquals(true, isSquared);
    }
}
